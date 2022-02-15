package com.rigsit.xanitizer.plugins.languages.python;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import com.rigsit.xanitizer.pub.configitems.IConfigurationItemGUI;
import com.rigsit.xanitizer.pub.configitems.IConfigurationItemGUI.GUIElement;
import com.rigsit.xanitizer.pub.configitems.IConfigurationItemRegistry;
import com.rigsit.xanitizer.pub.configitems.IPersistenceParser;
import com.rigsit.xanitizer.pub.util.regex.FileMatcher;

/**
 * @author Jonathan Kramme
 *
 */
public class PythonConfigurationEntries {

	// final static String ELEMENT_NAME__Python_TAINT_ANALYSIS_TIMEOUT =
	// "Python_TAINT_ANALYSIS_TIMEOUT";
	// final static int DEFAULT__Python_TAINT_ANALYSIS_TIMEOUT = 60 * 60;

	final static String ELEMENT_NAME__Python_BATCH_SIZE = "Python_BATCH_SIZE";
	final static int DEFAULT__Python_BATCH_SIZE = 1000;

	final static String ELEMENT_NAME__Python_FILE_MATCHER = "Python_FILE_MATCHER";
	final static String DEFAULT__Python_FILE_MATCHER =

			"+**.py;+**.pyw;"

	; // NOTE: Python files aren't required to have the extension (except for Windows
		// users).

	final static String ELEMENT_NAME__Python_WORKSPACE_FILE_MATCHER = "Python_WORKSPACE_FILE_MATCHER";
	final static String DEFAULT__Python_WORKSPACE_FILE_MATCHER = ""

			+ "+**;"

			+ "-**/site-packages/**;\n"

//			+ "-**/node_modules/**;\n-**/bower_components/**;\n-**/jspm_packages/**;\n"
//
//			+ "-**/lib/**;\n-**/sdk/**;\n-**/dist/**;\n-**/target/**;\n"
//
//			+ "-**.min.js;\n-**/polyfills*.js;\n-**/runtime*.js;\n-**/styles*.js;\n-**/vendor*.js;\n"
//			
	;

	public static void registerPythonConfigurationItems(final IConfigurationItemRegistry configurationItemRegistry) {

		// Python timeout in seconds.
		/*
		 * configurationItemRegistry.registerConfigurationItem(
		 * DEFAULT__Python_TAINT_ANALYSIS_TIMEOUT,
		 * ELEMENT_NAME__Python_TAINT_ANALYSIS_TIMEOUT, "Taint Analysis Timeout",
		 * "Number of seconds that the Python taint analysis runs maximally (zero means: unlimited)"
		 * , IPersistenceParser.INTEGER, new IConfigurationItemGUI() {
		 * 
		 * @Override public GUIElement getGUIElement() { return GUIElement.SPINNER; }
		 * 
		 * public int getMaximumValueForSpinner() { return 3600; } });
		 * 
		 */
		// Python Batch size.
		configurationItemRegistry.registerConfigurationItem(DEFAULT__Python_BATCH_SIZE, ELEMENT_NAME__Python_BATCH_SIZE,
				"Batch Size", "Number of Python files to be processed in one run (zero means: unlimited)",
				IPersistenceParser.INTEGER, new IConfigurationItemGUI() {

					@Override
					public GUIElement getGUIElement() {
						return GUIElement.SPINNER;
					}

					public int getMaximumValueForSpinner() {
						return 100000;
					}
				});

		// Python workspace and library file matcher.
		configurationItemRegistry.registerConfigurationItem(DEFAULT__Python_FILE_MATCHER,
				ELEMENT_NAME__Python_FILE_MATCHER, "Python files",
				"Matcher for Python files, consisting of a semicolon-separated list of patterns."
						+ " Patterns starting with '+' are inclusion patterns, and '-' marks exclusion patterns."
						+ " In a pattern, wildcards '**', '*' and '?' can be used, directory separators are '/',"
						+ " and directories to be matched end in '/'."
						+ "\n\nThis matcher describes both workspace and library Python files.",
				IPersistenceParser.STRING, new IConfigurationItemGUI() {
					@Override
					public GUIElement getGUIElement() {
						return GUIElement.TEXT_FIELD;
					}

					@Override
					public String validate(final Object currentValue) {
						if (!(currentValue instanceof String)) {
							return "Unexpected data";
						}

						try {
							FileMatcher.parse((String) currentValue);
							return null;
						} catch (final PatternSyntaxException ex) {
							return "Invalid matcher: " + ex.getMessage();
						}
					}
				});

		configurationItemRegistry.registerConfigurationItem(DEFAULT__Python_WORKSPACE_FILE_MATCHER,
				ELEMENT_NAME__Python_WORKSPACE_FILE_MATCHER, "Python Workspace Files",
				"Matcher for files to be considered as Python workspace, consisting of a semicolon-separated list of patterns."
						+ " Patterns starting with '+' are inclusion patterns, and '-' marks exclusion patterns."
						+ " In a pattern, wildcards '**', '*' and '?' can be used, directory separators are '/',"
						+ " and directories to be matched end in '/'."
						+ "\n\nNote that the base set of files are defined by the \"Python files\" matcher, this "
						+ "file matcher is applied to files matched by the other to check if it is a workspace file or not.",
				IPersistenceParser.STRING, new IConfigurationItemGUI() {
					@Override
					public GUIElement getGUIElement() {
						return GUIElement.WORKSPACE_PATTERN;
					}

					@Override
					public String validate(final Object currentValue) {
						if (!(currentValue instanceof String)) {
							return "Unexpected data";
						}
						try {
							FileMatcher.parse((String) currentValue);
							return null;
						} catch (final PatternSyntaxException ex) {
							return "Invalid matcher: " + ex.getMessage();
						}
					}
				});

	}

}