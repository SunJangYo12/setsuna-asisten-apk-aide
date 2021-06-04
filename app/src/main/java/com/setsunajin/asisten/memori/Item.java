package com.setsunajin.asisten.memori;

public class Item {
	private int id;
	private String title;
	
	public Item(int id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
}