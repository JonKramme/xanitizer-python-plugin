package com.rigsit.xanitizer.plugins.languages.python;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigsit.xanitizer.pub.plugin.ILanguageContext;
import com.rigsit.xanitizer.pub.util.ISourceLineData;
import com.rigsit.xanitizer.pub.util.IXFile;

/**
 * @author Jonathan Kramme
 *
 */
public final class PythonJSONParser {
	String ProblemTypePrefix;
	private final static Logger LOG = Logger.getLogger(PythonLanguagePlugin.class.getName());
	static final Map<String, Integer> CONFIDENCE = new HashMap<String, Integer>();

	PythonJSONParser(String string) {
		this.ProblemTypePrefix = string;

		// CONFIDENCE

		CONFIDENCE.put("LOW", 10);
		CONFIDENCE.put("MEDIUM", 20);
		CONFIDENCE.put("HIGH", 30);
	}

	public void parseAndRegister(ILanguageContext context, File pyTaintDir) {
		File JsonPath = new File((pyTaintDir == null ? "" : pyTaintDir.toString()) + "/banditreport.json");

		try {
			// https://www.baeldung.com/jackson-json-node-tree-model
			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(JsonPath);

			JsonNode jsonNode1 = actualObj.get("results");

			for (JsonNode node : jsonNode1) {
			
				try {
					final String problemTypeId = ProblemTypePrefix + BanditTestMap.getTestIDByKey(node.findValue("test_id").textValue());
					// System.out.println("ProblemTypeID: "+ problemTypeId);

					final var problemType = context.getActiveProblemTypeForIDOrNull(problemTypeId);
					
					IXFile lastXFile = context.getFactory().mkXFileFromAbsoluteFile(node.findValue("filename").textValue());
					int lastLine = node.findValue("line_number").intValue();
					String oneLineDescription = node.findValue("issue_text").textValue();
					List<ISourceLineData> sourceLineData = new ArrayList<>();
					JsonNode linerange = node.findValue("line_range");
					for (JsonNode linenum : linerange) {
						sourceLineData.add(context.getFactory().mkSourceLineData(lastXFile, linenum.asInt(), oneLineDescription, true, 0,null/* TODO:fact? */));						
					}
					int confidence = CONFIDENCE.get(node.findValue("issue_confidence").textValue());
					String presentationName = "Python Finding";
					String categoryDesc = BanditTestMap.getDescByKey(node.findValue("test_id").textValue());

					if (problemType == null) {
						continue;
					}
					context.registerFinding(problemType, lastXFile, lastLine, oneLineDescription, sourceLineData,
							presentationName, categoryDesc, false, confidence);
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Error while parsing single Python finding", e);
				}
			}

		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Error while parsing Python findings JSON", e);
		}
	}

}
