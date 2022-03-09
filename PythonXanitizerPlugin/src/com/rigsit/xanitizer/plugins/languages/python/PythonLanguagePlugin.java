/**
 * 
 */
package com.rigsit.xanitizer.plugins.languages.python;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rigsit.xanitizer.pub.configitems.IConfigurationItemRegistry;
import com.rigsit.xanitizer.pub.plugin.AbstractPlugin;
import com.rigsit.xanitizer.pub.plugin.ILanguageContext;
import com.rigsit.xanitizer.pub.plugin.ILanguagePlugin;
import com.rigsit.xanitizer.pub.plugin.ILanguagePluginRegistry;
import com.rigsit.xanitizer.pub.plugin.ISearchPath;
import com.rigsit.xanitizer.pub.plugin.IXanitizerPlugin;
import com.rigsit.xanitizer.pub.plugin.PluginID;
import com.rigsit.xanitizer.pub.problemtypes.IProblemType;
import com.rigsit.xanitizer.pub.util.FileUtil;
import com.rigsit.xanitizer.pub.util.ICancelListener;
import com.rigsit.xanitizer.pub.util.ISearchPathKind;
import com.rigsit.xanitizer.pub.util.ISourceLineData;
import com.rigsit.xanitizer.pub.util.IXFile;
import com.rigsit.xanitizer.pub.util.PluginSearchPathKind;
import com.rigsit.xanitizer.pub.util.ProgrammingLanguage;
import com.rigsit.xanitizer.pub.util.regex.FileMatcher;

/**
 * @author Jonathan Kramme
 *
 */


// Central Class for the Python Language Plugin
public class PythonLanguagePlugin extends AbstractPlugin implements ILanguagePlugin, IXanitizerPlugin {

	private final static Logger LOG = Logger.getLogger(PythonLanguagePlugin.class.getName());

	private final static String WORKSPACE_FILES_CATEGORY = "Python Workspace Files";
	private final static String PY_PROBLEM_DEFINITIONS_DIR_WITH_SLASHES = "/PythonProblemDefinitions/";
	private static File ProjectBaseDir;
	private final static PluginSearchPathKind PY_BASE_DIR_SEARCH_PATH_KIND = new PluginSearchPathKind(
			getPluginID_static(), "python base dir", "Python Base Directory",
			"Base directory containing Python source files to be analyzed", false, ProgrammingLanguage.PYTHON) {

		@Override
		public boolean checkFile(final Path fileOrDirToCheck) {
			return Files.isDirectory(fileOrDirToCheck);
		}
	};

	private Map<File, File> m_BaseDirsForGeneratedPythonFilesToOriginalBaseDirs;
	private Map<Path, List<List<String>>> m_WorkspaceFilesInBatches;
	private File m_PYTaintWorkDir;

	private final Set<FindingKey> m_SeenFindingKeys = new HashSet<>();

	public PythonLanguagePlugin() throws Exception {
		LOG.info("Found plugin '" + getPluginID() + "'");
	}

	@Override
	public void resetData() {
		if (null != m_PYTaintWorkDir) {
			FileUtil.deleteRecursively(m_PYTaintWorkDir);
			m_PYTaintWorkDir = null;
		}
		if (m_BaseDirsForGeneratedPythonFilesToOriginalBaseDirs != null) {
			m_BaseDirsForGeneratedPythonFilesToOriginalBaseDirs.clear();
		}
		m_BaseDirsForGeneratedPythonFilesToOriginalBaseDirs = null;
		m_WorkspaceFilesInBatches = null;
		m_SeenFindingKeys.clear();
	}
	
	//clears/resets the temporary working directory
	private void resetTmpWorkDir(final ILanguageContext languageContext) {
		m_PYTaintWorkDir = new File(languageContext.getProjectDirectory(), "pytaintWork");
		FileUtil.deleteRecursively(m_PYTaintWorkDir);
		m_PYTaintWorkDir.mkdirs();
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public PluginID getPluginID() {
		return getPluginID_static();
	}

	private static PluginID getPluginID_static() {
		return new PluginID("Python");
	}

	@Override
	public String getPresentationName() {
		return "Python";
	}

	@Override
	public String getDescription() {
		return "Find problems in Python code";
	}

	@Override
	public void registerAdditionalConfigurationItems(final IConfigurationItemRegistry configurationItemRegistry) {
		PythonConfigurationEntries.registerPythonConfigurationItems(configurationItemRegistry);
	}

	@Override
	public Collection<ISearchPathKind> getSearchPathKinds() {
		return Arrays.asList(PY_BASE_DIR_SEARCH_PATH_KIND);
	}

	@Override
	public Collection<ISearchPath> collectSearchPaths(final Path root, final ILanguageContext context,
			final ICancelListener cancelListener) {
		if (!Files.isDirectory(root)) {
			return Collections.emptyList();
		}

		ProjectBaseDir = root.toFile();

		final var foundSomething = new boolean[1];

		collectPythonFiles(ProjectBaseDir, _fileBeneath -> foundSomething[0] = true, context, cancelListener);

		if (foundSomething[0]) {
			return Collections.singletonList(context.mkSearchPath(PY_BASE_DIR_SEARCH_PATH_KIND, ProjectBaseDir));
		}
		return Collections.emptyList();
	}
	
	//Collects all the Python files in the project folder and registers them in Xanitizer
	private void collectPythonFiles(final File baseDir, final Consumer<String> callback, final ILanguageContext context,
			final ICancelListener dummyCancelListener) {
		final var relativePathAntMatcherText_Python = context.getPluginDefinedXanitizerConfigurationValue(
				PythonConfigurationEntries.ELEMENT_NAME__Python_FILE_MATCHER,
				PythonConfigurationEntries.DEFAULT__Python_FILE_MATCHER);

		final var relativePathAntMatcherText_Workspace = context.getPluginDefinedXanitizerConfigurationValue(
				PythonConfigurationEntries.ELEMENT_NAME__Python_WORKSPACE_FILE_MATCHER,
				PythonConfigurationEntries.DEFAULT__Python_WORKSPACE_FILE_MATCHER);

		FileUtil.collectRelativePathMatches(baseDir, FileMatcher.parse(relativePathAntMatcherText_Python),
				FileMatcher.parse(relativePathAntMatcherText_Workspace), callback, null /* libraryAccuOrNull */,
				dummyCancelListener);
	}

	@Override
	public void registerFindings(ILanguageContext context, ICancelListener cancelListener) {

		/*
		 * If the analysis is re-calculated, we have to forget the already seen
		 * findings.
		 */
		m_SeenFindingKeys.clear();

		final Path workspace = context.getProjectDirectory().toPath();

		cancelListener.showProgressMessage("Running external tool Bandit..");
		try {
			PythonAnalysisRunner.runBandit(workspace, getPythonProblemTypeIdPrefixEndingInSlash(),m_PYTaintWorkDir, context,
					cancelListener, System.out, System.err);
		} catch (IOException | InterruptedException e) {
			LOG.log(Level.SEVERE, "Error while executing Bandit", e);
		}
		cancelListener.showProgressMessage("Parsing Bandit Results");
		new PythonJSONParser(getPythonProblemTypeIdPrefixEndingInSlash()).parseAndRegister(context, m_PYTaintWorkDir);
	}

	@Override
	public ProgrammingLanguage getProgrammingLanguage() {

		return ProgrammingLanguage.PYTHON;
	}

	@Override
	public void registerWorkspaceResources(final ILanguageContext context, final ICancelListener cancelListener) {
		m_BaseDirsForGeneratedPythonFilesToOriginalBaseDirs = new LinkedHashMap<>();

		resetTmpWorkDir(context);
		/*
		 * If the analysis is re-calculated, we have to forget the already seen
		 * findings.
		 */
		m_SeenFindingKeys.clear();

		PythonMetricCalculator metric = new PythonMetricCalculator();
		m_WorkspaceFilesInBatches = new LinkedHashMap<>();

		cancelListener.showProgressMessage("Collecting Python Workspace Files");
		mkBatchesOfPythonFiles(context, m_BaseDirsForGeneratedPythonFilesToOriginalBaseDirs.keySet(), cancelListener);
		cancelListener.showProgressMessage("");

		cancelListener.showProgressMessage("Registration of Python Workspace Files");
		m_WorkspaceFilesInBatches.forEach((basePath, batches) -> {
			batches.forEach(batch -> {
				batch.forEach(file -> {
					cancelListener.throwExceptionIfCancelled("While registering Python workspace files");
					final Path workspaceFile = basePath.resolve(file);
					metric.addMetricforPath(workspaceFile);
					registerInWorkspaceIfNecessary(workspaceFile, context);
				});
			});
		});

		final var metricValueAccu = new HashMap<String, Integer>();
		// ... and accumulate the metric values.
		metricValueAccu.put("CM_PROJECT_SOURCE_FILES",
				metric.getNumberOfFiles() + metricValueAccu.getOrDefault("CM_PROJECT_SOURCE_FILES", 0));
		metricValueAccu.put("CM_NON_EMPTY_PROJECT_METHODS",
				metric.getNumberOfFunctions() + metricValueAccu.getOrDefault("CM_NON_EMPTY_PROJECT_METHODS", 0));// Might
																													// be
		// Empty.
		// Emptiness
		// Check not
		// implemented
		metricValueAccu.put("CM_PROJECT_LOC",
				metric.getNumberOfLocs() + metricValueAccu.getOrDefault("CM_PROJECT_LOC", 0));
		metricValueAccu.put("CM_PROJECT_METHODS_ALL",
				metric.getNumberOfFunctions() + metricValueAccu.getOrDefault("CM_PROJECT_METHODS_ALL", 0));

		for (final var e : metricValueAccu.entrySet()) {
			context.registerMetricValueChange(e.getKey(), e.getValue());
		}

		cancelListener.showProgressMessage("");

	}

	private void mkBatchesOfPythonFiles(final ILanguageContext context,
			final Collection<File> baseDirsForGeneratedPythonFiles, final ICancelListener cancelListener) {
		final var searchPathDirs = context.mkSearchPathFiles(PY_BASE_DIR_SEARCH_PATH_KIND);
		final var combinedBaseDirs = new ArrayList<File>(searchPathDirs);
		combinedBaseDirs.addAll(baseDirsForGeneratedPythonFiles);

		for (final var baseDir : combinedBaseDirs) {
			final var workspaceFiles = new ArrayList<String>();

			collectPythonFiles(baseDir, fileBeneath -> workspaceFiles.add(fileBeneath), context, cancelListener);

			if (workspaceFiles.isEmpty()) {
				continue;
			}

			// Make results more deterministic.
			Collections.sort(workspaceFiles);

			final var workDir = Paths.get(baseDir.getPath());

			final var batchSize = context.getPluginDefinedXanitizerConfigurationValue(
					PythonConfigurationEntries.ELEMENT_NAME__Python_BATCH_SIZE,
					PythonConfigurationEntries.DEFAULT__Python_BATCH_SIZE);

			if (null == m_WorkspaceFilesInBatches) {
				m_WorkspaceFilesInBatches = new LinkedHashMap<>();
			}
			if (batchSize <= 0) {
				final var oneBatch = new LinkedList<List<String>>();
				oneBatch.add(workspaceFiles);
				m_WorkspaceFilesInBatches.put(workDir, oneBatch);
			} else {
				final var oneSetOfBatches = new ArrayList<List<String>>();
				m_WorkspaceFilesInBatches.put(workDir, oneSetOfBatches);
				final var sz = workspaceFiles.size();
				var numProcessed = 0;
				while (numProcessed < sz) {
					final var batch = new ArrayList<String>();
					int i;
					for (i = 0; i < batchSize && numProcessed + i < sz; ++i) {
						batch.add(workspaceFiles.get(numProcessed + i));
					}
					numProcessed += i;
					oneSetOfBatches.add(batch);
				}
			}
		}
	}

	private static void registerInWorkspaceIfNecessary(final Path absPath, final ILanguageContext context) {

		if (Files.exists(absPath)) {
			final var fileUri = absPath.toUri();
			if (!fileUri.toString().contains(PY_PROBLEM_DEFINITIONS_DIR_WITH_SLASHES)) {
				final var xFile = context.getFactory().mkXFileFromAbsoluteFile(absPath.toString());
				try {
					context.registerWorkspaceResource(WORKSPACE_FILES_CATEGORY, xFile, fileUri.toURL());
				} catch (final MalformedURLException e) {
					LOG.log(Level.WARNING, "Exception computing url", e);
				}
			}
		}
	}

	@Override
	public List<File> getLibraries(ILanguageContext context, ICancelListener cancelListener) {
		return new ArrayList<File>(context.mkSearchPathFiles(PY_BASE_DIR_SEARCH_PATH_KIND));
	}

	@Override
	public void registerAdditionalProblemTypesAndFrameworks(ILanguagePluginRegistry registry) {

		if (BanditTestMap.exists()) {
			for (final Entry<String, BanditTest> testProblem : BanditTestMap.getEntrySet()) {
				String id = mkPyTaintProblemTypeId(testProblem.getValue().getTestID());
				registry.registerProblemType(this, id, true /* warningRatherThanInfo */,
						testProblem.getValue().getPresentationName(), testProblem.getValue().getDesc(),
						testProblem.getValue().getHowToFix(), testProblem.getValue().getCweNumber(),
						testProblem.getValue().getDefaultRating(), true /* isOnByDefault */,
						false /* Paths not implemented for Python */);
			}
		}

	}

	@Override
	public boolean workspaceParsingNecessary(ILanguageContext ctxt) {
		return m_BaseDirsForGeneratedPythonFilesToOriginalBaseDirs == null || m_WorkspaceFilesInBatches == null;
	}

	private static class FindingKey {
		private final String m_ProblemTypeId;
		private final IXFile m_XFile;
		private final int m_Line;
		private final String m_Desc;
		private final List<ISourceLineData> m_SourceLineData;

		public FindingKey(final IProblemType pt, final IXFile xFile, final int line, final String desc,
				final List<ISourceLineData> sourceLineData) {
			m_ProblemTypeId = pt.getId();
			m_XFile = xFile;
			m_Line = line;
			m_Desc = desc;
			m_SourceLineData = sourceLineData;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((m_Desc == null) ? 0 : m_Desc.hashCode());
			result = prime * result + m_Line;
			result = prime * result + ((m_ProblemTypeId == null) ? 0 : m_ProblemTypeId.hashCode());
			result = prime * result + ((m_SourceLineData == null) ? 0 : m_SourceLineData.hashCode());
			result = prime * result + ((m_XFile == null) ? 0 : m_XFile.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FindingKey other = (FindingKey) obj;
			if (m_Desc == null) {
				if (other.m_Desc != null)
					return false;
			} else if (!m_Desc.equals(other.m_Desc))
				return false;
			if (m_Line != other.m_Line)
				return false;
			if (m_ProblemTypeId == null) {
				if (other.m_ProblemTypeId != null)
					return false;
			} else if (!m_ProblemTypeId.equals(other.m_ProblemTypeId))
				return false;
			if (m_SourceLineData == null) {
				if (other.m_SourceLineData != null)
					return false;
			} else if (!m_SourceLineData.equals(other.m_SourceLineData))
				return false;
			if (m_XFile == null) {
				if (other.m_XFile != null)
					return false;
			} else if (!m_XFile.equals(other.m_XFile))
				return false;
			return true;
		}
	}

	@Override
	public Collection<String> getConfigurationOptionsForInitialSetup() {
		return Collections.emptyList();

	}

	String mkPyTaintProblemTypeId(final String pytaintId) {
		return getPythonProblemTypeIdPrefixEndingInSlash() + pytaintId;
	}

	private String getPythonProblemTypeIdPrefixEndingInSlash() {
		return getClass().getName() + "/pyTaint/";
	}

}
