package com.rigsit.xanitizer.plugins.languages.python;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Jonathan Kramme
 *
 */
public class PythonMetricCalculator {

	private int m_numberOfFiles;
	private int m_numberOfLocs;
	private int m_numberOfFunctions;

	public PythonMetricCalculator() {

		this.m_numberOfFiles = 0;
		this.m_numberOfLocs = 0;
		this.m_numberOfFunctions = 0;
	}

	public void addMetricforPath(Path workspaceFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(workspaceFile.toAbsolutePath().toFile()));
			boolean commentActive = false;

			String line = reader.readLine();

			while (line != null) {

				if (line.contains("'''")) {
					commentActive = !commentActive;
				}
				if (!commentActive && !line.isBlank()) {
					m_numberOfLocs++;
					if (line.contains("def")) {
						m_numberOfFunctions++;
					}
					if (line.contains("lambda")) {
						m_numberOfFunctions++;
					}
				}

				line = reader.readLine(); // Get next line
			}
			m_numberOfFiles++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getNumberOfFiles() {
		return m_numberOfFiles;
	}

	public int getNumberOfLocs() {
		return m_numberOfLocs;
	}

	public int getNumberOfFunctions() {
		return m_numberOfFunctions;
	}

}
