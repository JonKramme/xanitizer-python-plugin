package com.rigsit.xanitizer.plugins.languages.python;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Jonathan Kramme
 *
 */
public final class BanditTestMap {

	private static final HashMap<String, BanditTest> pythonTestMap = createPythonTestMap();

	public static Set<Entry<String, BanditTest>> getEntrySet() {
		return pythonTestMap.entrySet();
	}

	public static boolean exists() {
		if (BanditTestMap.pythonTestMap != null) {
			return true;
		}
		return false;
	}

	public static Set<String> getKeyByID(String value) {
		Set<String> keys = new HashSet<>();
		for (Entry<String, BanditTest> entry : pythonTestMap.entrySet()) {
			if (entry.getValue().getTestID().equals(value)) {
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	public static String getDescByKey(String key) {
		String temp = pythonTestMap.get(key).getDesc();
		return (temp != null ? temp : "");
	}

	public static String getTestIDByKey(String key) {
		String temp = pythonTestMap.get(key).getTestID();
		return (temp != null ? temp : "");
	}
	/* deprecatated for "getDescByKey" and "getTestIDByKey" to avoid NPE when returning null instead of BanditTest Object.
	public static BanditTest getValueByKey(String key) {
		return pythonTestMap.get(key);
	}
	 */
	
	// TODO: Automatic Test Parsing from Bandit source
	// TODO: Methods to get BanditTestID (ex. B203) from TestID (
	// IMPROPER_CHECK_OF_EXCEPT_COND)
	private static final HashMap<String, BanditTest> createPythonTestMap() {
		HashMap<String, BanditTest> tempMap = new HashMap<String, BanditTest>();
		// Removed "unimplemented" bandit tests to be able to read the Map correctly in
		// tandem with Problemtype activation.
		// tempMap.put("B000", new BanditTest("IDNOTSET", -1,-1, "DESCRIPTIONNOTSET",
		// "HOWTOFIXNOTSET"));
		// tempMap.put("LEGACY", new BanditTest("IDNOTSET", -1,-1, "DESCRIPTIONNOTSET",
		// "HOWTOFIXNOTSET"));

		tempMap.put("B101", new BanditTest("IMPROPER_CHECK_OF_EXCEPT_COND", 703, 3,
				"Use of assert detected. The enclosed code will be removed when compiling to optimised byte code.",
				"Please see:\n" + " - https://bugs.launchpad.net/juniperopenstack/+bug/1456193\n"
						+ " - https://bugs.launchpad.net/heat/+bug/1397883\n"
						+ " - https://docs.python.org/3/reference/simple_stmts.html#the-assert-statement"));
		tempMap.put("B102",
				new BanditTest("OS_COMMAND_INJECTION", 78, 6, "Use of exec detected.",
						"Please see:\n" + " - https://docs.python.org/2/reference/simple_stmts.html#exec\n"
								+ " - https://docs.python.org/3/library/functions.html#exec\n"
								+ " - https://www.python.org/dev/peps/pep-0551/#background\n"
								+ " - https://www.python.org/dev/peps/pep-0578/#suggested-audit-hook-locations"));
		tempMap.put("B103", new BanditTest("INCORRECT_PERMISSION_ASSIGNMENT", 732, 9,
				"Chmod setting a permissive mask %s on file (%s).",
				"Please see:\n"
						+ " - https://security.openstack.org/guidelines/dg_apply-restrictive-file-permissions.html\n"
						+ " - https://en.wikipedia.org/wiki/File_system_permissions\n"
						+ " - https://security.openstack.org"));
		tempMap.put("B104", new BanditTest("MULTIPLE_BINDS", 605, 6, "Possible binding to all interfaces.",
				"Please see:\n" + " - https://nvd.nist.gov/vuln/detail/CVE-2018-1281"));
		tempMap.put("B105", new BanditTest("HARD_CODED_PASSWORD", 259, 3, "Possible hardcoded password.",
				"Please see:\n" + " - https://www.owasp.org/index.php/Use_of_hard-coded_password"));
		tempMap.put("B106", new BanditTest("HARD_CODED_PASSWORD", 259, 3, "Possible hardcoded password.",
				"Please see:\n" + " - https://www.owasp.org/index.php/Use_of_hard-coded_password"));
		tempMap.put("B107", new BanditTest("HARD_CODED_PASSWORD", 259, 3, "Possible hardcoded password.",
				"Please see:\n" + " - https://www.owasp.org/index.php/Use_of_hard-coded_password"));
		tempMap.put("B108", new BanditTest("INSECURE_TEMP_FILE", 377, 6,
				"Probable insecure usage of temp file/directory.", "Please see:\n"
						+ " - https://security.openstack.org/guidelines/dg_using-temporary-files-securely.html"));
		tempMap.put("B110", new BanditTest("IMPROPER_CHECK_OF_EXCEPT_COND", 703, 3, "Try, Except, Pass detected.",
				"Please see:\n" + " - https://security.openstack.org"));
		tempMap.put("B112", new BanditTest("IMPROPER_CHECK_OF_EXCEPT_COND", 703, 3, "Try, Except, Continue detected.",
				"Please see:\n" + " - https://security.openstack.org"));

		tempMap.put("B201", new BanditTest("CODE_INJECTION", 94, 9,
				"A Flask app appears to be run with debug=True, which exposes the Werkzeug debugger and allows the execution of arbitrary code.",
				"Please see:\n" + " - https://flask.palletsprojects.com/en/1.1.x/quickstart/#debug-mode\n"
						+ " - https://werkzeug.palletsprojects.com/en/1.0.x/debug/\n"
						+ " - https://labs.detectify.com/2015/10/02/how-patreon-got-hacked-publicly-exposed-werkzeug-debugger/"));

		tempMap.put("B324", new BanditTest("BROKEN_CRYPTO", 327, 6, "Use of insecure MD4 or MD5 hash function.", ""));
		// "Please see:\n"
		// + "howtofix"));

		tempMap.put("B501",
				new BanditTest("IMPROPER_CERT_VALIDATION", 295, 9,
						"Requests call with verify=False disabling SSL certificate checks, security issue.",
						"Please see:\n" + " - https://security.openstack.org/guidelines/dg_move-data-securely.html\n"
								+ " - https://security.openstack.org/guidelines/dg_validate-certificates.html"));
		tempMap.put("B502", new BanditTest("BROKEN_CRYPTO", 327, 9,
				"call with insecure SSL/TLS protocol version identified, security issue.",
				"Please see:\n" + "     - :func:`ssl_with_bad_defaults`\n" + "     - :func:`ssl_with_no_version`\n"
						+ "     - https://heartbleed.com/\n" + "     - https://en.wikipedia.org/wiki/POODLE\n"
						+ "     - https://security.openstack.org/guidelines/dg_move-data-securely.html"));
		tempMap.put("B503", new BanditTest("BROKEN_CRYPTO", 327, 6,
				"Function definition identified with insecure SSL/TLS protocol version by default, possible security issue.",
				"Please see:\n" + "     - :func:`ssl_with_bad_version`\n" + "     - :func:`ssl_with_no_version`\n"
						+ "     - https://heartbleed.com/\n" + "     - https://en.wikipedia.org/wiki/POODLE\n"
						+ "     - https://security.openstack.org/guidelines/dg_move-data-securely.html"));
		tempMap.put("B504", new BanditTest("BROKEN_CRYPTO", 327, 3,
				"ssl.wrap_socket call with no SSL/TLS protocol version specified, the default SSLv23 could be insecure, possible security issue.",
				"Please see:\n" + "     - :func:`ssl_with_bad_version`\n" + "     - :func:`ssl_with_bad_defaults`\n"
						+ "     - https://heartbleed.com/\n" + "     - https://en.wikipedia.org/wiki/POODLE\n"
						+ "     - https://security.openstack.org/guidelines/dg_move-data-securely.html"));
		tempMap.put("B505",
				new BanditTest("INADEQUATE_ENCRYPTION_STRENGTH", 326, 9,
						"DSA and RSA key sizes below 1024 bits are considered breakable.\n"
								+ "EC Keys length size of 160 and below are considered breakable",
						" The recommended key length size for RSA and DSA algorithms is 2048 and higher.\n"
								+ "EC key length sizes are recommended to be 224 and higher.\n" + "Please see:\n"
								+ " - https://csrc.nist.gov/publications/detail/sp/800-131a/rev-2/final\n"
								+ " - https://security.openstack.org/guidelines/dg_strong-crypto.html"));
		tempMap.put("B506", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 6,
				"Use of unsafe yaml load. Allows instantiation of arbitrary objects.", "Consider yaml.safe_load().\n"
						+ "Please see:\n" + " - https://pyyaml.org/wiki/PyYAMLDocumentation#LoadingYAML"));
		tempMap.put("B507", new BanditTest("IMPROPER_CERT_VALIDATION", 295, 9,
				"Paramiko call with policy set to automatically trust the unknown host key.", ""));
		// "Please see:\n"
		// + "howtofix"));

		tempMap.put("B601",
				new BanditTest("OS_COMMAND_INJECTION", 78, 6,
						"Possible shell injection via Paramiko call, check inputs are properly sanitized.",
						"Please see:\n" + " - https://security.openstack.org\n"
								+ " - https://github.com/paramiko/paramiko\n"
								+ " - https://www.owasp.org/index.php/Command_Injection"));
		tempMap.put("B602",
				new BanditTest("OS_COMMAND_INJECTION", 78, 9,
						"subprocess call with shell=True identified, security issue.",
						"Please see:\n" + " - https://security.openstack.org\n"
								+ " - https://docs.python.org/3/library/subprocess.html#frequently-used-arguments\n"
								+ " - https://security.openstack.org/guidelines/dg_use-subprocess-securely.html\n"
								+ " - https://security.openstack.org/guidelines/dg_avoid-shell-true.html"));
		tempMap.put("B603",
				new BanditTest("OS_COMMAND_INJECTION", 78, 3,
						"subprocess call - check for execution of untrusted input.",
						"Please see:\n" + "     - https://security.openstack.org\n"
								+ "     - https://docs.python.org/3/library/subprocess.html#frequently-used-arguments\n"
								+ "     - https://security.openstack.org/guidelines/dg_avoid-shell-true.html\n"
								+ "     - https://security.openstack.org/guidelines/dg_use-subprocess-securely.html"));
		tempMap.put("B604",
				new BanditTest("OS_COMMAND_INJECTION", 78, 6,
						"Function call with shell=True parameter identified, possible security issue.",
						"Please see\n" + "     - https://security.openstack.org/guidelines/dg_avoid-shell-true.html\n"
								+ "     - https://security.openstack.org/guidelines/dg_use-subprocess-securely.html"));
		tempMap.put("B605",
				new BanditTest("OS_COMMAND_INJECTION", 78, 9,
						"Starting a process with a shell, possible injection detected, security issue.",
						"Please see\n" + "     - https://security.openstack.org\n"
								+ "     - https://docs.python.org/3/library/os.html#os.system\n"
								+ "     - https://docs.python.org/3/library/subprocess.html#frequently-used-arguments\n"
								+ "     - https://security.openstack.org/guidelines/dg_use-subprocess-securely.html"));
		tempMap.put("B606",
				new BanditTest("OS_COMMAND_INJECTION", 78, 3, "Starting a process without a shell.",
						"Please see:\n" + "     - https://security.openstack.org\n"
								+ "     - https://docs.python.org/3/library/os.html#os.system\n"
								+ "     - https://docs.python.org/3/library/subprocess.html#frequently-used-arguments\n"
								+ "     - https://security.openstack.org/guidelines/dg_use-subprocess-securely.html"));
		tempMap.put("B607",
				new BanditTest("OS_COMMAND_INJECTION", 78, 3, "Starting a process with a partial executable path",
						"Please see:\n" + "     - https://security.openstack.org\n"
								+ "     - https://docs.python.org/3/library/os.html#process-management"));
		tempMap.put("B608",
				new BanditTest("SQL_INJECTION", 89, 6,
						"Possible SQL injection vector through string-based query construction.",
						"Please see:\n" + " - https://www.owasp.org/index.php/SQL_Injection\n"
								+ " - https://security.openstack.org/guidelines/dg_parameterize-database-queries.html\n"
								+ ""));
		tempMap.put("B609",
				new BanditTest("IMPROPER_WILDCARD_NEUTRALIZATION", 155, 9, "Possible wildcard injection in call",
						"Please see:\n" + " - https://security.openstack.org\n"
								+ " - https://en.wikipedia.org/wiki/Wildcard_character\n"
								+ " - https://www.defensecode.com/public/DefenseCode_Unix_WildCards_Gone_Wild.txt"));
		tempMap.put("B611", new BanditTest("SQL_INJECTION", 89, 6, "Use of RawSQL potential SQL attack vector.",
				"Please see:\n" + "     - https://docs.djangoproject.com/en/dev/topics/security/\\"));

		tempMap.put("B701", new BanditTest("CODE_INJECTION", 94, 9,
				"Using jinja2 templates with autoescape=False is dangerous and can lead to XSS.",
				"Ensure autoescape=True or use the select_autoescape function to mitigate XSS vulnerabilities.\n"
						+ "Please see:\n"
						+ " - `OWASP XSS <https://www.owasp.org/index.php/Cross-site_Scripting_(XSS)>`_\n"
						+ " - https://realpython.com/primer-on-jinja-templating/\n"
						+ " - https://jinja.palletsprojects.com/en/2.11.x/api/#autoescaping\n"
						+ " - https://security.openstack.org/guidelines/dg_cross-site-scripting-xss.html"));
		tempMap.put("B702",
				new BanditTest("BASIC_XSS", 80, 6,
						"Mako templates allow HTML/JS rendering by default and are inherently open to XSS attacks.",
						"Ensure variables in all templates are properly sanitized via the 'n', 'h' or 'x' "
								+ "flags (depending on context). For example, "
								+ "to HTML escape the variable 'data' do ${ data |h }." + "Please see:\n"
								+ " - https://www.makotemplates.org/\n"
								+ " - `OWASP XSS <https://owasp.org/www-community/attacks/xss/>`_\n"
								+ " - https://security.openstack.org/guidelines/dg_cross-site-scripting-xss.html"));
		tempMap.put("B703", new BanditTest("BASIC_XSS", 80, 6, "Potential XSS on mark_safe function.", "Please see:\n"
				+ "     - https://docs.djangoproject.com/en/dev/topics/security/\\\n"
				+ "#cross-site-scripting-xss-protection\n"
				+ "     - https://docs.djangoproject.com/en/dev/ref/utils/\\\n" + "#module-django.utils.safestring\n"
				+ "     - https://docs.djangoproject.com/en/dev/ref/utils/\\\n" + "#django.utils.html.format_html"));

		// Calls
		tempMap.put("B301", new BanditTest("DESERIALIZATION_OF_UNTRUSTED_DATA", 502, 6,
				"Pickle and modules that wrap it can be unsafe when used to deserialize untrusted data, possible security issue.",
				""));
		tempMap.put("B302", new BanditTest("DESERIALIZATION_OF_UNTRUSTED_DATA", 502, 6,
				"Deserialization with the marshal module is possibly dangerous.", ""));
		tempMap.put("B303",
				new BanditTest("BROKEN_CRYPTO", 327, 6, "Use of insecure MD2, MD4, MD5, or SHA1 hash function.", ""));
		tempMap.put("B304", new BanditTest("BROKEN_CRYPTO", 327, 9, "Use of insecure cipher or cipher mode.",
				"Replace with a known secure cipher such as AES."));
		tempMap.put("B305", new BanditTest("BROKEN_CRYPTO", 327, 6, "Use of insecure cipher or cipher mode.",
				"Replace with a known secure cipher such as AES."));
		tempMap.put("B306",
				new BanditTest("INSECURE_TEMP_FILE", 377, 6, "Use of insecure and deprecated function (mktemp).", ""));
		tempMap.put("B307", new BanditTest("OS_COMMAND_INJECTION", 78, 6, "Use of possibly insecure function",
				"consider using safer ast.literal_eval."));
		tempMap.put("B308", new BanditTest("XSS", 79, 6,
				"Use of mark_safe() may expose cross-site scripting vulnerabilities", "It should be reviewed."));
		tempMap.put("B309", new BanditTest("CLEARTEXT_TRANSMISSION", 319, 6,
				"Use of HTTPSConnection on older versions of Python prior to 2.7.9 and 3.4.3 do not provide security",
				"Please see:\n" + " - https://wiki.openstack.org/wiki/OSSN/OSSN-0033"));
		tempMap.put("B310", new BanditTest("PATH_TRAVERSAL", 22, 6,
				"Audit url open for permitted schemes. Allowing use of 'file:'' or custom schemes is often unexpected.",
				""));
		tempMap.put("B311", new BanditTest("INSUFFICIENT_RANDOM_VALUES", 330, 3,
				"Standard pseudo-random generators are not suitable for security/cryptographic purposes.", ""));
		tempMap.put("B312",
				new BanditTest("CLEARTEXT_TRANSMISSION", 319, 9,
						"Telnet-related functions are being called. Telnet is considered insecure.",
						"Use SSH or some other encrypted protocol."));
		tempMap.put("B313", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 6,
				"Using various XLM methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
				"Methods should be replaced with their defusedxml equivalents."));
		tempMap.put("B314", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 6,
				"Using various XLM methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
				"Methods should be replaced with their defusedxml equivalents."));
		tempMap.put("B315", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 6,
				"Using various XLM methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
				"Methods should be replaced with their defusedxml equivalents."));
		tempMap.put("B316", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 6,
				"Using various XLM methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
				"Methods should be replaced with their defusedxml equivalents."));
		tempMap.put("B317", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 6,
				"Using various XLM methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
				"Methods should be replaced with their defusedxml equivalents."));
		tempMap.put("B318", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 6,
				"Using various XLM methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
				"Methods should be replaced with their defusedxml equivalents."));
		tempMap.put("B319", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 6,
				"Using various XLM methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
				"Methods should be replaced with their defusedxml equivalents."));
		tempMap.put("B320", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 6,
				"Using various XLM methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
				"Methods should be replaced with their defusedxml equivalents."));
		tempMap.put("B321",
				new BanditTest("CLEARTEXT_TRANSMISSION", 319, 9,
						"FTP-related functions are being called. FTP is considered insecure",
						"Use SSH/SFTP/SCP or some other encrypted protocol."));
//		tempMap.put("B322", new BanditTest("OS_COMMAND_INJECTION", 78 ,9,
//				"The check for this call has been removed.\n"
//				+ "\n"
//				+ "The input method in Python 2 will read from standard input, evaluate and run the "
//				+ "resulting string as python source code. This is similar, though in many ways worse, than using eval.",
//				"On Python 2, use raw_input instead, input is safe in Python 3."));
		tempMap.put("B323", new BanditTest("IMPROPER_CERT_VALIDATION", 295, 6,
				"By default, Python will create a secure, verified ssl context for use in such classes as "
						+ "HTTPSConnection. However, it still allows using an insecure context via the _create_unverified_context"
						+ "that reverts to the previous behavior that does not validate certificates or perform hostname checks.",
				""));
		tempMap.put("B325",
				new BanditTest("INSECURE_TEMP_FILE", 377, 6,
						"Use of os.tempnam() and os.tmpnam() is vulnerable to symlink attacks.",
						"Consider using tmpfile() instead.\n" + "Please see:\n"
								+ " - https://docs.python.org/2.7/library/os.html#os.tempnam\n"
								+ " - https://docs.python.org/3/whatsnew/3.0.html?highlight=tempnam\n"
								+ " - https://bugs.python.org/issue17880"));

		// Imports
		tempMap.put("B401",
				new BanditTest("CLEARTEXT_TRANSMISSION", 319, 9,
						"A telnet-related module is being imported. Telnet is considered insecure.",
						"Use SSH or some other encrypted protocol."));
		tempMap.put("B402",
				new BanditTest("CLEARTEXT_TRANSMISSION", 319, 9,
						"A FTP-related module is being imported.  FTP is considered insecure.",
						"Use SSH/SFTP/SCP or some other encrypted protocol."));
		tempMap.put("B403",
				new BanditTest("DESERIALIZATION_OF_UNTRUSTED_DATA", 502, 3,
						"A Pickle-related module is being imported.",
						"Consider possible security implications associated with these modules."));
		tempMap.put("B404",
				new BanditTest("OS_COMMAND_INJECTION", 78, 3, "A subprocess-related module is being imported.",
						"Consider possible security implications associated with these modules."));
		tempMap.put("B405",
				new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 3,
						"Using various methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
						"Replace vulnerable imports with the equivalent defusedxml package, or make sure"
								+ " defusedxml.defuse_stdlib() is called."));
		tempMap.put("B406",
				new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 3,
						"Using various methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
						"Replace vulnerable imports with the equivalent defusedxml package, or make sure"
								+ " defusedxml.defuse_stdlib() is called."));
		tempMap.put("B407",
				new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 3,
						"Using various methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
						"Replace vulnerable imports with the equivalent defusedxml package, or make sure"
								+ " defusedxml.defuse_stdlib() is called."));
		tempMap.put("B408",
				new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 3,
						"Using various methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
						"Replace vulnerable imports with the equivalent defusedxml package, or make sure"
								+ " defusedxml.defuse_stdlib() is called."));
		tempMap.put("B409",
				new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 3,
						"Using various methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
						"Replace vulnerable imports with the equivalent defusedxml package, or make sure"
								+ " defusedxml.defuse_stdlib() is called."));
		tempMap.put("B410",
				new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 3,
						"Using various methods to parse untrusted XML data is known to be vulnerable to XML attacks.",
						"Replace vulnerable imports with the equivalent defusedxml package."));
		tempMap.put("B411", new BanditTest("IMPROPER_INPUT_VALIDATION", 20, 9,
				"XMLRPC is particularly dangerous as it is also concerned with communicating data over a network.",
				"Use defused.xmlrpc.monkey_patch() function to monkey-patch xmlrpclib and mitigate remote XML attacks."));
		tempMap.put("B412", new BanditTest("IMPROPER_ACCESS_CONTROL", 284, 9,
				"httpoxy is a set of vulnerabilities that affect application code running in CGI, or "
						+ "CGI-like environments. The use of CGI for web applications should be avoided to prevent"
						+ " this class of attack.",
				"Please see:\n" + " - https://httpoxy.org/"));
		tempMap.put("B413", new BanditTest("BROKEN_CRYPTO", 327, 9,
				"pycrypto library is known to have publicly disclosed buffer overflow vulnerability\n" + "Please see:\n"
						+ " - https://github.com/dlitz/pycrypto/issues/176"
						+ "It is no longer actively maintained and has been deprecated in favor of pyca/cryptography library.",
				"Replace with pyca/cryptography library"));
//		tempMap.put("B414", new BanditTest("BROKEN_CRYPTO", 327,9,
//				"This import blacklist has been removed. The information here has been "
//				+ "left for historical purposes.\n"
//				+ "\n"
//				+ "pycryptodome is a direct fork of pycrypto that has not fully addressed "
//				+ "the issues inherent in PyCrypto.  It seems to exist, mainly, as an API " 
//				+ "compatible continuation of pycrypto and should be deprecated in favor "
//				+ "of pyca/cryptography which has more support among the Python community.",
//				"Replace with pyca/cryptography library"));
		return tempMap;
	}

}
