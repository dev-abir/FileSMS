package org.ralph.filesms;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MessageFileReader {

	private String filePath;
	private int countryCode;

	private Error errorObject;

	private String currentLine;

	private File file;
	private Scanner sc;
	private boolean endOfFile;

	public MessageFileReader(String filePath, int countryCode) {
		this.filePath = filePath;
		this.countryCode = countryCode;
	}

	public String getNextNumber() {
		if (sc.hasNextLine()) {
			currentLine = sc.nextLine();
		} else {
			endOfFile = true;
		}
		String number = "";
		if (currentLine == null) {
			//This line is blank(has no numbers)
			try {
				return getNextNumber();
			} catch (StackOverflowError e) {
				errorObject = new Error(Error.TYPE_VERY_LONG_GAP_BETWEEN_TWO_NUMBERS_OR_NO_NUMBERS);
				errorObject.setErrorMessage("Stack Overflow Error! There is a very long gap between two numbers, or there are no numbers(Just blank lines).");
			}
		} else {
			number = currentLine;
			number = number.replaceAll(" ", "").replaceAll("-", "");
			if (number.charAt(0) == '+') {
				number = number.substring(1);
			}
			if (number.length() > 12) {
				errorObject = new Error(Error.TYPE_VERY_LONG_NUMBER);
				errorObject.setErrorMessage(number + " is very long.");
			} else {
				if (number.length() == 10) {
					number = Integer.toString(countryCode) + number;
				}
				if (number.length() < 10) {
					errorObject = new Error(Error.TYPE_NUMBER_IS_TOO_SHORT);
					errorObject.setErrorMessage(number + " is too short.");
				}
			}
		}
		if (!sc.hasNextLine()) {
			endOfFile = true;
		}
		return number;
	}

	public String getCurrentNumber() {
		return currentLine;
	}

	public void initReader() {
		/**
		 *
		 * NOTE THE LINE GIVEN BELOW...
		 *
		 */
		file = new File(Environment.getExternalStorageDirectory(), filePath);
		/**
		 *
		 *NOTE THE LINE ABOVE...
		 *
		 */
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			errorObject = new Error(Error.TYPE_FILE_NOT_FOUND);
			errorObject.setErrorMessage(filePath + "not found.");
			//e.printStackTrace();
		}
	}

	public Error getErrorObject() {
		return errorObject;
	}

	public boolean isEndOfFile() {
		return endOfFile;
	}

	public void clearResources() {
		sc.close();
	}
}
