package com.di.jmeter.pubsub.sampler;

import java.io.Serializable;

import org.apache.jmeter.gui.Searchable;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestStateListener;

public abstract class PublisherTestElement extends AbstractTestElement implements TestStateListener, TestElement, Serializable, Searchable {


	private static final long serialVersionUID = 7027549399338665744L;
	
	private boolean gzipCompression;
	private String message;
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isGzipCompression() {
		return gzipCompression;
	}

	public void setGzipCompression(boolean gzipCompression) {
		this.gzipCompression = gzipCompression;
	}


}
