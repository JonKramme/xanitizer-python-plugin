package com.rigsit.xanitizer.plugins.languages.python;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.rigsit.xanitizer.pub.plugin.ILanguageContext;
import com.rigsit.xanitizer.pub.plugin.ISystemContext;
import com.rigsit.xanitizer.pub.util.ICancelListener;
import com.rigsit.xanitizer.pub.util.ProcessUtil;

/**
 * @author Jonathan Kramme
 *
 */
public class PythonAnalysisRunner {
	private final static Logger LOG = Logger.getLogger(PythonAnalysisRunner.class.getName());
	private static String banditExe = "bandit-linux";

	public static int runBandit(final Path baseDirOrNull, final String problemTypePrefix, final File PYTaintWorkDir,  final ILanguageContext context,
			final ICancelListener cancelListener, final OutputStream osOut, final OutputStream osErr)
			throws IOException, InterruptedException {

		if (context.isWindows()) {
			banditExe = "bandit.exe";
		}
		if (baseDirOrNull != null) { // no point in running bandit if there is no base dir?
			final var cmdLine = new LinkedList<String>();
			cmdLine.add(context.getInstallDirectory() + "/xanitizer-plugins/tools/" + banditExe);
			cmdLine.add("-r");

			String tests = collectTestStrings(context, problemTypePrefix);

			if (!tests.isBlank()) {
				cmdLine.add("-t");
				cmdLine.add(tests);
			} else {
				// No Tests selected.
				return -1;
			}
			cmdLine.add((baseDirOrNull == null ? "" : baseDirOrNull.toString()));
			cmdLine.add("-o");
			cmdLine.add(PYTaintWorkDir.getAbsolutePath()+ "/banditreport.json");
			cmdLine.add("-f");
			cmdLine.add("json");

			// run Bandit.exe -r ~/your_repos/project

			try {
				String cmd = "";
				for (String x : cmdLine) {
					cmd += x + " ";
				}
				System.out.println(cmd);
				return ProcessUtil.exec(cmdLine.toArray(new String[cmdLine.size()]), null /* envp */, null, osOut,
						osErr, cancelListener);
			} finally {
			}
		}
		return -1;
	}

	private static String collectTestStrings(ILanguageContext context, String problemTypePrefix) {

		final StringBuilder sbtests = new StringBuilder("");
		for (String id : context.getActiveProblemTypesWithIdPrefix(problemTypePrefix)) {
			for (String testId : BanditTestMap.getKeyByID(id.replace(problemTypePrefix, "")))
				// removes the Prefix from the Problemtype
				sbtests.append(testId + ",");
		}
		// remove trailing comma added above.
		return sbtests.toString().substring(0, sbtests.length() - 1);
	}
}
