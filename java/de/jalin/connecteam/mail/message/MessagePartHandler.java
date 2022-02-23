package de.jalin.connecteam.mail.message;


import de.jalin.connecteam.etc.CxException;
import jakarta.mail.BodyPart;

public interface MessagePartHandler {

	public void handle(BodyPart part) throws CxException;
	
}
