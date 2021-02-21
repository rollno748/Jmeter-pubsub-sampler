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

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import com.di.jmeter.pubsub.config.PublisherConfig;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.util.ConfigMergabilityIndicator;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPOutputStream;

public class PublisherSampler extends PublisherTestElement implements Sampler, TestBean, ConfigMergabilityIndicator {

	private static final long serialVersionUID = -2509242423429019193L;
	private static final Logger LOGGER = LoggerFactory.getLogger(PublisherSampler.class);
	private static final Gson gson = new Gson();
	private static final Type TYPED_MAP = new TypeToken<Map<String, String>>() {}.getType();

	private Publisher publisher = null;
	private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
			Arrays.asList("org.apache.jmeter.config.gui.SimpleConfigGui"));

	@Override
	public SampleResult sample(Entry e) {
		PubsubMessage template = null;
		SampleResult result = new SampleResult();
		result.setSampleLabel(getName());
		result.setSamplerData(request());
		result.setDataType(SampleResult.TEXT);
		result.setContentType("text/plain");
		result.setDataEncoding(StandardCharsets.UTF_8.name());

		Map<String, String> attributes = convertStringToAttributesMap(getAttributes());

		if (isGzipCompression()) {
			template = createPubsubMessage(createEventCompressed(getMessage()), attributes);
		} else {
			template = convertStringToPubSubMessage(getMessage(), attributes);
		}

		result.sampleStart();

		try {
			publish(template, result);
		} catch (Exception ex) {
			LOGGER.info("Exception occurred while publishing message");
			result = handleException(result, ex);
		} finally {
			result.sampleEnd();
		}
		return result;
	}

	private Map<String,String> convertStringToAttributesMap(String attributesAsString){
			return Optional.ofNullable(attributesAsString)

					.map(str -> {
						Map<String, String> result = null;
						try {
							result = gson.fromJson(str, TYPED_MAP);
						} catch (JsonSyntaxException sse) {
							LOGGER.error("Failed to convert string attributes: {} to Map<String,String>", attributesAsString, sse);
						}
						return result;
					})

					.orElse(Collections.emptyMap());
	}

	// Returns Modified templates/Message as template for publishing
	private PubsubMessage convertStringToPubSubMessage(String message, Map<String, String> attributes) {
		return PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(message)).putAllAttributes(attributes).build();
	}

	private byte[] createEventCompressed(String message) {
		//BufferedWriter zipWriter = null;
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(message.length())) {
			try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
				gzipOutputStream.write(message.getBytes(StandardCharsets.UTF_8));
				gzipOutputStream.close();
			}
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("Failed to zip content", e);
		}
	}

	private String publish(PubsubMessage template, SampleResult result) {
		String resp = null;
		ApiFuture<String> future = null;
		if (this.publisher == null) {
			this.publisher = PublisherConfig.getPublisherClient();
		}

		try {
			future = publisher.publish(template);
			result.setResponseHeaders("MessagePublishedID: "+ future.get());
			result.setResponseData(template.toString(), StandardCharsets.UTF_8.name());
			result.setSuccessful(true);
			result.setResponseCode("200");
			result.setResponseMessageOK();
			
		} catch (ExecutionException e) {
			LOGGER.info("Publisher config not initialized properly.. Check the config element");
			handleException(result, e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return resp;
	}

	public static PubsubMessage createPubsubMessage(byte[] msg, Map<String,String> attributes) {
		return PubsubMessage.newBuilder().setData(ByteString.copyFrom(msg)).putAllAttributes(attributes).build();
	}

	private String request() {
		StringBuilder requestBody = new StringBuilder();
		requestBody.append("PublishedMessage: \n").append(getMessage()).append("\n");
		return requestBody.toString();
	}

	private SampleResult handleException(SampleResult result, Exception ex) {
		result.setResponseMessage("Message Publish Error");
		result.setResponseCode("500");
		result.setResponseData(String.format("Error in publishing message to PubSub topic : %s", ex.toString()).getBytes());// PublisherConfig.getTopic()
		result.setSuccessful(false);
		return result;
	}

	@Override
	public boolean applies(ConfigTestElement configElement) {
		String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
		return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
	}

	@Override
	public void testStarted() {
		// TODO Auto-generated method stub
	}

	@Override
	public void testStarted(String host) {
		// TODO Auto-generated method stub
	}

	@Override
	public void testEnded() {
		// TODO Auto-generated method stub
	}

	@Override
	public void testEnded(String host) {
		// TODO Auto-generated method stub
	}

}
