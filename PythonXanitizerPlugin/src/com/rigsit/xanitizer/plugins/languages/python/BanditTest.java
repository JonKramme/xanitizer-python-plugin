package com.rigsit.xanitizer.plugins.languages.python;

import java.util.stream.Stream;


/**
 * @author Jonathan Kramme
 *
 */
public class BanditTest {
	private String m_testID;
	private String m_presentationName;
	private String m_desc;
	private String m_howToFix;
	private int m_cweNumber;
	private int m_defaultRating;

	BanditTest(String testID, int cweNumber, int defaultRating, String desc, String howToFix) {
		this.m_testID = testID;
		this.m_presentationName = "Python: " + mkPresentationName(testID);
		this.m_cweNumber = cweNumber;
		this.m_defaultRating = defaultRating;
		this.m_desc = desc;
		this.m_howToFix = howToFix;

	}

	private String mkPresentationName(String testID) {

		String presentationName = titleCaseConversion(testID.replace('_', ' '));
		
		
		return presentationName;
	}

    private static String titleCaseConversion(String inputString) 
    {
        if (inputString.isBlank()) {
            return "";
        }
 
        if (inputString.length() == 1) {
            return inputString.toUpperCase();
        }
 
        StringBuffer resultPlaceHolder = new StringBuffer(inputString.length());
 
        Stream.of(inputString.split(" ")).forEach(stringPart -> 
        {
            if (stringPart.length() > 1)
                resultPlaceHolder.append(stringPart.substring(0, 1)
                                    .toUpperCase())
                                    .append(stringPart.substring(1)
                                    .toLowerCase());
            else
                resultPlaceHolder.append(stringPart.toUpperCase());
 
            resultPlaceHolder.append(" ");
        });
        return resultPlaceHolder.toString().trim();
    }
	
	public String getTestID() {
		return m_testID;
	}

	public String getPresentationName() {
		return m_presentationName;
	}

	public String getDesc() {
		return m_desc;
	}

	public String getHowToFix() {
		return m_howToFix;
	}

	public int getCweNumber() {
		return m_cweNumber;
	}

	public int getDefaultRating() {
		return m_defaultRating;
	}
}
