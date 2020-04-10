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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.util.ConfigMergabilityIndicator;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.di.jmeter.pubsub.config.SubscriberConfig;
import com.di.jmeter.pubsub.utils.MessagesQueue;
import com.google.pubsub.v1.PubsubMessage;

public class SubscriberSampler extends SubscriberTestElement implements Sampler, TestBean, ConfigMergabilityIndicator {

	private static final long serialVersionUID = 7189759970130154866L;
	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberSampler.class);
	private static final Set<String> APPLIABLE_CONFIG_CLASSES = new HashSet<>(
			Arrays.asList("org.apache.jmeter.config.gui.SimpleConfigGui"));

	private MessagesQueue messagesQueue;
	private static PubsubMessage reader;
	private String ackDelay;
	private boolean decompression;

	@Override
	public SampleResult sample(Entry e) {
		SampleResult result = new SampleResult();
		result.setSampleLabel(getName());
		// result.setSamplerData();
		result.setDataType(SampleResult.TEXT);
		result.setContentType("text/plain");
		result.setDataEncoding(StandardCharsets.UTF_8.name());

		if (Integer.parseInt(getAckDelay()) > 1) {
			ackDelay = getAckDelay();
		}

		result.sampleStart();

		try {
			reader = readMessageFromTopic(result);

		} catch (Exception ex) {
			LOGGER.info("Exception Occurred while reading message");
			result = handleException(result, ex);
		} finally {
			result.sampleEnd();
		}
		return result;
	}

	private PubsubMessage readMessageFromTopic(SampleResult result) throws IOException {

		if (messagesQueue == null) {
			this.messagesQueue = SubscriberConfig.getMessagesQueue();
		}

		try {
			reader = messagesQueue.take();
			if (isDecompression()) {
				result.setResponseData(createDeCompressedMessage(reader.getData().toByteArray()),
						StandardCharsets.UTF_8.name());
			} else {
				result.setResponseData(reader.getData().toString(), StandardCharsets.UTF_8.name());
			}
			result.setResponseHeaders("PublishedMessageID: " + reader.getMessageId() + "\npublish_time in "+reader.getPublishTime());
			result.setSuccessful(true);
			result.setResponseCode("200");
			result.setResponseMessageOK();

		} catch (InterruptedException e) {
			LOGGER.info(String.format("Error in reading message from the Message queue " + e));
			e.printStackTrace();
		}

		return reader;
	}

	private String createDeCompressedMessage(byte[] message) throws IOException {
		String output = null;
		StringBuffer resultBuffer = new StringBuffer();
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message)) {
			try (GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream)) {
				BufferedReader br = new BufferedReader(new InputStreamReader(gZIPInputStream));
				while ((output = br.readLine()) != null) {
					resultBuffer.append(output);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultBuffer.toString();
	}

	private SampleResult handleException(SampleResult result, Exception ex) {
		result.setResponseMessage("Message Read Error");
		result.setResponseCode("500");
		result.setResponseData(
				String.format("Error in Retrieving message from the receiever queue: " + ex.toString()).getBytes());
		result.setSuccessful(false);
		return result;
	}

	@Override
	public void testStarted() {
		// TODO Auto-generated method stub
	}

	@Override
	public void testStarted(String host) {
		testStarted();
	}

	@Override
	public void testEnded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void testEnded(String host) {
		testEnded();
	}

	@Override
	public boolean applies(ConfigTestElement configElement) {
		String guiClass = configElement.getProperty(TestElement.GUI_CLASS).getStringValue();
		return APPLIABLE_CONFIG_CLASSES.contains(guiClass);
	}

	public String getAckDelay() {
		return ackDelay;
	}

	public void setAckDelay(String ackDelay) {
		this.ackDelay = ackDelay;
	}

	public boolean isDecompression() {
		return decompression;
	}

	public void setDecompression(boolean decompression) {
		this.decompression = decompression;
	}

}
