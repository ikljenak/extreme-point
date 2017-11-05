package com.thesis.app.models;

public interface Packable {
	
	public boolean pack(Item item);
	
	public Packable resetCopy(Container container);

}
