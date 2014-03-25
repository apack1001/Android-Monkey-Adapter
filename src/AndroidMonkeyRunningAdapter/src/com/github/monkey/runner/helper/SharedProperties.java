package com.github.monkey.runner.helper;

import java.util.ArrayList;

public class SharedProperties {

	private static ArrayList<Object> properties = new ArrayList<Object>();
	
	private static SharedProperties instance = new SharedProperties();
	
	public static SharedProperties getInstance() {
		return instance;
	}
	
	private SharedProperties() {
		
	}
	
	public void add(final Object property) {
		synchronized (properties) {
			properties.add(property);
		}
	}
	
	public void clear() {
		synchronized (properties) {
			properties.clear();
		}
	}
	
	public ArrayList<Object> getProperties() {
		return properties;
	}
	
}
