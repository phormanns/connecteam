package de.jalin.connecteam.data;

public class Subscriber {

	private long id;
	private String address;
	private String name;
	
	public Subscriber() {
		id = 0L;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
