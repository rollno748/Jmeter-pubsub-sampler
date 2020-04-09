package com.di.jmeter.pubsub.sampler;

import java.beans.PropertyDescriptor;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.TestBean;

public class SubscriberTestElementBeanInfoSupport extends BeanInfoSupport {
	
	protected SubscriberTestElementBeanInfoSupport(Class<? extends TestBean> beanClass) {
		super(beanClass);

		createPropertyGroup("SubscriberProperties", new String[] { "ackDelay", "decompression"});
		 
        PropertyDescriptor propertyDescriptor =  property("ackDelay");
        propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
        propertyDescriptor.setValue(DEFAULT, "0");
        
        propertyDescriptor =  property("decompression");
        propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
        propertyDescriptor.setValue(DEFAULT, Boolean.FALSE);
        
	}

}
