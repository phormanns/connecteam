package de.jalin.connecteam.mail.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Text2Html {

	private static final String REGEXP_HTTP_TEXT_LINK = "(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
	private static final String HTTP_LINK_REPLACEMENT = "<a href=\"$1\" target=\"_new\">$1</a>";
	
	public String getFormattedText(final String plainText) {
		final int maxlen = 84;
		final StringBuffer formated = new StringBuffer("<p>\n");
		int blank = maxlen - 10;
		boolean isPreFormatted = false;
		try {
			final BufferedReader reader = new BufferedReader(new StringReader(plainText));
			String s = reader.readLine();
			while (s != null) { // Schleife liest zeilenweise
				if (s.trim().isEmpty()) {
					formated.append("</p>\n");
					formated.append("<p>\n");
					isPreFormatted = false;
				} else {
					isPreFormatted = s.startsWith(">") || s.startsWith(" ") || s.startsWith("--") || s.startsWith("==");
					if (isPreFormatted) {
						formated.append("<br/>\n");
					}
					isPreFormatted = isPreFormatted || (s.length() < 56);
				}
				while (s.length() > maxlen) { // lange Zeile werden zerlegt
					blank = maxlen - 10; // Blank vor dem Umbruch suchen
					while (blank < s.length() && s.charAt(blank) != ' ') {
						blank++;
					}
					formated.append(replaceEntities(s.substring(0, blank)).replaceAll(REGEXP_HTTP_TEXT_LINK, HTTP_LINK_REPLACEMENT));
					formated.append('\n');
					if (isPreFormatted) {
						formated.append("<br/>\n");
					}
					s = s.substring(blank);
					while (s.length() > 0 && s.charAt(0) == ' ') {
						s = s.substring(1); // ggf. fuehrende Blank abschneiden
					}
				}
				formated.append(replaceEntities(s).replaceAll(REGEXP_HTTP_TEXT_LINK, HTTP_LINK_REPLACEMENT));
				if (s.endsWith("--") || s.endsWith("-- ") || s.endsWith("==") || isPreFormatted) {
					formated.append("<br/>\n");
				}
				formated.append('\n');
				s = reader.readLine(); // naechste Zeile
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		formated.append("</p>\n");
		return formated.toString();
	}

	public String replaceEntities(String src) {
		src = replaceString(src, "&", "&amp;");
		src = replaceString(src, "<", "&lt;");
		src = replaceString(src, ">", "&gt;");
		src = replaceString(src, "\"", "&quot;");
		return src;
	}

	private final String replaceString(final String orig, final String src, final String dest) {
		if (orig == null) {
			return null;
		}
		if (src == null || dest == null) {
			throw new NullPointerException();
		}
		if (src.length() == 0) {
			return orig;
		}
		final StringBuffer res = new StringBuffer(orig.length() + 20); 
		int start = 0;
		int end = 0;
		int last = 0;
		while ((start = orig.indexOf(src, end)) != -1) {
			res.append(orig.substring(last, start));
			res.append(dest);
			end = start + src.length();
			last = start + src.length();
		}
		res.append(orig.substring(end));
		return res.toString();
	}

	
}
