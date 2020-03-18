package org.ralph.filesms;

public class Error {

	private int type;
	private String errorMessage;

	public static final int TYPE_VERY_LONG_NUMBER = 123;
	public static final int TYPE_IO_EXCEPTION = 456;
	public static final int TYPE_FILE_NOT_FOUND = 789;
	public static final int TYPE_VERY_LONG_GAP_BETWEEN_TWO_NUMBERS_OR_NO_NUMBERS = 727;
	public static final int TYPE_NUMBER_IS_TOO_SHORT = 225;

	public Error(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
