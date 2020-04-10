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
