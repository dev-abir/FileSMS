package org.ralph.filesms;

public class SMS /*implements Comparable*/ {

	public static final int MESSAGE_SENDING_ONGOING = 45698;
	public static final int MESSAGE_SENDING_FAILED = 45694;
	public static final int MESSAGE_SENDING_SUCCESS = 45705;
	public static final int NO_STATUS = 10;

	private String name;
	private String number;
	private String message;
	private int messageSendingStatus;
	private String failedReason = "";

	// Constructor with name
	public SMS(String name, String number, String message) {
		this.name = name;
		this.number = number;
		this.message = message;
	}

	// Constructor with name
	public SMS(String number, String message) {
		this.name = ""; // TODO : Check for errors.
		this.number = number;
		this.message = message;
	}

	public int getMessageSendingStatus() {
		return messageSendingStatus;
	}

	public void setMessageSendingStatus(int messageSendingStatus) {
		this.messageSendingStatus = messageSendingStatus;
	}

	public void setFailedReason(String failedReason) {
		if (messageSendingStatus == MESSAGE_SENDING_FAILED) this.failedReason = failedReason;
		else //TODO : FATAL ERROR....
			return;
	}

	public String getMessage() {
		return message;
	}

	public String getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SMS{" +
				"name='" + name + '\'' +
				", number='" + number + '\'' +
				", message='" + message + '\'' +
				", messageSendingStatus=" + messageSendingStatus +
				", failedReason='" + failedReason + '\'' +
				'}';
	}
}
