package com.di.jmeter.pubsub.sampler;

import java.beans.PropertyDescriptor;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.gui.TypeEditor;

public class PublisherTestElementBeanInfoSupport extends BeanInfoSupport {

	protected PublisherTestElementBeanInfoSupport(Class<? extends TestBean> beanClass) {
		super(beanClass);

		createPropertyGroup("Message to publish", new String[] { "gzipCompression","message"});
		
		PropertyDescriptor propertyDescriptor =  property("message", TypeEditor.TextAreaEditor);
        propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
        propertyDescriptor.setValue(DEFAULT, "{\"demoMessage\":\"Hello World!\"}");
		 
        propertyDescriptor =  property("gzipCompression");
        propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
        propertyDescriptor.setValue(DEFAULT, Boolean.FALSE);
        
	}

}
