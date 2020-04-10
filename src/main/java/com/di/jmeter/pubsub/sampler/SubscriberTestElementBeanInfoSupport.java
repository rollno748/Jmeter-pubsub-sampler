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
