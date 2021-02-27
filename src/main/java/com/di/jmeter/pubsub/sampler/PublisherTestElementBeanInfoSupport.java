/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.di.jmeter.pubsub.sampler;

import java.beans.PropertyDescriptor;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.gui.TypeEditor;

public class PublisherTestElementBeanInfoSupport extends BeanInfoSupport {

	protected PublisherTestElementBeanInfoSupport(Class<? extends TestBean> beanClass) {
		super(beanClass);

		createPropertyGroup("Message to publish", new String[] { "publisherClientObject", "gzipCompression", "attributes", "message"});
		
		PropertyDescriptor propertyDescriptor =  property("message", TypeEditor.TextAreaEditor);
        propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
        propertyDescriptor.setValue(DEFAULT, "{\"demoMessage\":\"Hello World from DI pubsub sampler!\"}");
        
        propertyDescriptor =  property("gzipCompression");
        propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
        propertyDescriptor.setValue(DEFAULT, Boolean.FALSE);
        
        propertyDescriptor =  property("publisherClientObject", TypeEditor.ComboStringEditor);
        propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
        propertyDescriptor.setValue(DEFAULT, "");
        
        propertyDescriptor = property("attributes", TypeEditor.TextAreaEditor);
        propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
        propertyDescriptor.setValue("default", "");
        
	}

}
