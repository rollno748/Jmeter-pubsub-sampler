package com.di.jmeter.pubsub.sampler;

import java.io.Serializable;

import org.apache.jmeter.gui.Searchable;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestStateListener;

public abstract class SubscriberTestElement extends AbstractTestElement implements TestStateListener, TestElement, Serializable, Searchable {

	private static final long serialVersionUID = -6951161193102820427L;
	private static String ackDelay;
	
	
// ===== Getters and Setters =====
	
	public String getAckDelay() {
		return ackDelay;
	}

	public void setAckDelay(String ackDelay) {
		if(Integer.parseInt(ackDelay)>0) {
			SubscriberTestElement.ackDelay = ackDelay;
		}else {
			SubscriberTestElement.ackDelay = "0";
		}
	}

}
