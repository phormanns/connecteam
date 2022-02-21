package de.jalin.connecteam.mail.loop;

public class Loop {

	private boolean isRunning;
	
	public Loop() { }
	
	public void start() {
		isRunning = true;
		while (isRunning) {
			
			System.out.print(".");
			
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				System.out.println("stop");
				isRunning = false;
			}
		}
	}
	
	public void stop() {
		System.out.println("stop");
		isRunning = false;
	}
	
	public static void main(String[] args) {
		Loop loop = new Loop();
		loop.start();
	}
	
}
