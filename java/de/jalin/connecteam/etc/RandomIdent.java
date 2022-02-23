package de.jalin.connecteam.etc;

import java.util.Random;

public class RandomIdent {

	private final String characterlist = "abcdefghijklmnopqrstuvwxyz0123456789";
	
	private static final Random rand = new Random();

	public synchronized String nextIdent() {
		return nextIdent(3, 6);
	}
	
	public synchronized String nextIdent(int outer, int inner) {
		final StringBuffer buf = new StringBuffer();
		int randomIndex = rand.nextInt(characterlist.length());
		for (int outerLoop = 0; outerLoop < outer; outerLoop++) {
			if (outerLoop > 0) buf.append('-');
			for (int innerLoop = 0; innerLoop < inner; innerLoop++) {
				randomIndex = rand.nextInt(characterlist.length());
				buf.append(characterlist.charAt(randomIndex));
			}
		}
		return buf.toString();
	}
	
	public static void main(String[] args) {
		RandomIdent ident = new RandomIdent();
		System.out.println(ident.nextIdent());
	}
}
