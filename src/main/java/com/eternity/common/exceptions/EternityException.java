package com.eternity.common.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EternityException extends Exception {
	private static final long serialVersionUID = 5251812675879861277L;
	
	private ArrayList<Exception> exceptions = new ArrayList<Exception>();

	protected int status = 500;
	
	public EternityException(){
		super("Unknown error!");
	}
	
	public EternityException(EternityException e){
		super("Multiple errors!");
		this.exceptions.addAll(e.exceptions);
	}
	
	public EternityException(Exception e){
		super(e);
		this.exceptions.add(e);
	}
	
	public EternityException(String string){
		super(string);
	}
	
	public EternityException(int status, String string) {
		super(string);
		this.status = status;
	}

	public int getHttpStatusEquivalent() {
		return status;
	}

	public List<String> getMessages(){
		ArrayList<String> messages = new ArrayList<String>(exceptions.size());
		for(Exception e : exceptions){
			messages.add(e.getMessage());
		}
		messages.add(getMessage());
		return Collections.unmodifiableList(messages);
	}
}
