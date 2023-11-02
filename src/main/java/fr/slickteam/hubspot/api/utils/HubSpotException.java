package fr.slickteam.hubspot.api.utils;

/**
 * Author: dlunev
 * Date: 7/27/15 9:43 AM
 */
public class HubSpotException extends Exception {

	/**
	 * Code of the exception
	 */
	private int code;

	/**
	 * Raw message from HubSpot
	 */
	private String rawMessage;

	/**
	 * Instantiates a new Hub spot exception.
	 *
	 * @param message the message
	 */
	public HubSpotException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Hub spot exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public HubSpotException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Hub spot exception.
	 *
	 * @param message the message
	 * @param code    the code
	 */
	public HubSpotException(String message, int code) {
		super(message);
		this.code = code;
	}

	/**
	 * Instantiates a new Hub spot exception.
	 *
	 * @param message    the message
	 * @param rawMessage the raw message
	 */
	public HubSpotException(String message, String rawMessage) {
		super(message);
		this.rawMessage = rawMessage;
	}

	/**
	 * Gets code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Gets raw message.
	 *
	 * @return the raw message
	 */
	public String getRawMessage() {
		return rawMessage;
	}

	/**
	 * Sets raw message.
	 *
	 * @param rawMessage the raw message
	 */
	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}
}
