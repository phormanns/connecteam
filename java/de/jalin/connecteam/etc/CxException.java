package de.jalin.connecteam.etc;

public class CxException extends Exception {

	private static final long serialVersionUID = 1L;

	public CxException(String message) {
		super(message);
	}
	
	public CxException(Throwable exception) {
		super(exception.getMessage(), exception);
	}
	
	public CxException(String message, Throwable exception) {
		super(message, exception);
	}
	
}
