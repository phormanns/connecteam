package de.jalin.connecteam.etc;

import java.util.ArrayList;
import java.util.List;

public class Space {

	private String name;
	private String description;
	private List<Mailinglist> lists;

	public Space() { 
		lists = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public List<Mailinglist> getLists() {
		return lists;
	}

	public void setLists(List<Mailinglist> lists) {
		this.lists = lists;
	}
}
