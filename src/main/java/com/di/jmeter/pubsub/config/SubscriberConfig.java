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

package com.di.jmeter.pubsub.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.TestBeanHelper;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.Duration;

import com.di.jmeter.pubsub.utils.MessagesQueue;
import com.di.jmeter.pubsub.utils.SimpleMessageReceiver;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.gson.JsonObject;
import com.google.pubsub.v1.ProjectSubscriptionName;

public class SubscriberConfig extends ConfigTestElement implements ConfigElement, TestStateListener, TestBean, Serializable {

	private static final long serialVersionUID = -6527581818773236163L;
	private static Logger LOGGER = LoggerFactory.getLogger(SubscriberConfig.class);
	private Subscriber subscriber;
	private transient JsonObject credentials = new JsonObject();
	private static GoogleCredentials gcpCredentials = null;

	private String type;
	private String topic;
	private String projectId;
	private String privateKey;
	private String privateKeyId;
	private String clientEmail;
	private String clientId;
	private String authUri;
	private String tokenUri;
	private String authProvider_x509CertUrl;
	private String client_x509CertUrl;
	private String subscriptionId;
	private boolean flowControlSetting;
	private String parallelPullCount;
	private String maxAckExtensionPeriod;
	private String maxOutStandingElementCount;
	private String maxOutstandingRequestBytes;
	private static MessagesQueue messagesQueue;

	private static String SUBSCRIBER_CONNECTION = "subscriberConnection";
	private static String MESSAGESQUEUE = "message";
	private static String SUBSCRIBED_TOPIC="subTopic";

	@Override
	public void testStarted() {
		this.setRunningVersion(true);
		TestBeanHelper.prepare(this);

		messagesQueue = new MessagesQueue(100000);
		JMeterVariables variables = getThreadContext().getVariables();
		ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(getProjectId(), getSubscriptionId());

		if (variables.getObject(SUBSCRIBER_CONNECTION) != null) {
			LOGGER.error("PubSub connection is already established and active !!");
		} else {
			synchronized (this) {
				try {

					LOGGER.info("Attempting to subscribe to a topic");
					if (isFlowControlSetting()) {
						subscriber = Subscriber.newBuilder(subscriptionName, new SimpleMessageReceiver(messagesQueue))
								.setCredentialsProvider(createCredentialsProviderUsingJson(getCredentials()))
								.setMaxAckExtensionPeriod(Duration.ofMillis(Long.parseLong(getMaxAckExtensionPeriod())))
								.setFlowControlSettings(flowControlSettings())
								.setParallelPullCount(Integer.parseInt(getParallelPullCount())).build();

					} else {
						subscriber = Subscriber.newBuilder(subscriptionName, new SimpleMessageReceiver(messagesQueue))
								.setCredentialsProvider(createCredentialsProviderUsingJson(getCredentials()))
								.setMaxAckExtensionPeriod(Duration.ofMillis(Long.parseLong(getMaxAckExtensionPeriod())))
								.build();

					}

					subscriber.startAsync().awaitRunning();
					// subscriber.awaitTerminated(); 
					// Allow the subscriber to run indefinitely unless an error occurs
					variables.putObject(SUBSCRIBER_CONNECTION, subscriber);
					variables.putObject(MESSAGESQUEUE, messagesQueue);
					variables.putObject(SUBSCRIBED_TOPIC, topic);
					LOGGER.info(String.format("Subscriber connection established with the %s successfully !!", getTopic()));

				} catch (IllegalStateException e) {
					LOGGER.info("Error occurred while establishing subscriber connection with Pub/Sub: " + e);
				}
			}
		}
	}

	private FlowControlSettings flowControlSettings() {
		return FlowControlSettings.newBuilder()
				.setMaxOutstandingElementCount(Long.valueOf(getMaxOutStandingElementCount()))
				.setMaxOutstandingRequestBytes(Long.valueOf(getMaxOutstandingRequestBytes())).build();
	}

	@Override
	public void testStarted(String host) {
		testStarted();
	}

	@Override
	public void testEnded() {
		synchronized (this) {
			if (subscriber != null) {
				subscriber.stopAsync();
				subscriber = null;
				LOGGER.info("Subscriber connection Terminated successfully !!");
			}
		}
	}

	@Override
	public void testEnded(String host) {
		testEnded();
	}

	@Override
	public void addConfigElement(ConfigElement config) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean expectsModification() {
		// TODO Auto-generated method stub
		return false;
	}

	// Return credentials in JSON Object
	private JsonObject getCredentials() {

		credentials.addProperty("type", getType());
		credentials.addProperty("topic", getTopic());
		credentials.addProperty("project_id", getProjectId());
		credentials.addProperty("private_key", getPrivateKey().replaceAll("\\\\n", "\n"));
		credentials.addProperty("private_key_id", getPrivateKeyId());
		credentials.addProperty("client_email", getClientEmail());
		credentials.addProperty("client_id", getClientId());
		credentials.addProperty("auth_uri", getAuthUri());
		credentials.addProperty("token_uri", getTokenUri());
		credentials.addProperty("auth_provider_x509_cert_url", getAuthProvider_x509CertUrl());
		credentials.addProperty("client_x509_cert_url", getClient_x509CertUrl());

		return credentials;
	}

	private static CredentialsProvider createCredentialsProviderUsingJson(JsonObject credentials) {

		InputStream configJson = new ByteArrayInputStream(credentials.toString().getBytes());

		try {
			gcpCredentials = GoogleCredentials.fromStream(configJson);
		} catch (IOException e) {
			System.out.println("Error occurred while creating GCPcredentials using Json");
			e.printStackTrace();
		}
		return FixedCredentialsProvider.create(gcpCredentials);
	}

// === Getters and Setters ===

	public String getType() {
		return type;
	}

	public String getTopic() {
		return topic;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getPrivateKeyId() {
		return privateKeyId;
	}

	public String getClientEmail() {
		return clientEmail;
	}

	public String getClientId() {
		return clientId;
	}

	public String getAuthUri() {
		return authUri;
	}

	public String getTokenUri() {
		return tokenUri;
	}

	public String getAuthProvider_x509CertUrl() {
		return authProvider_x509CertUrl;
	}

	public String getClient_x509CertUrl() {
		return client_x509CertUrl;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public boolean isFlowControlSetting() {
		return flowControlSetting;
	}

	public String getParallelPullCount() {
		return parallelPullCount;
	}

	public String getMaxOutstandingRequestBytes() {
		return maxOutstandingRequestBytes;
	}

	public String getMaxAckExtensionPeriod() {
		return maxAckExtensionPeriod;
	}

	public String getMaxOutStandingElementCount() {
		return maxOutStandingElementCount;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public void setPrivateKeyId(String privateKeyId) {
		this.privateKeyId = privateKeyId;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setAuthUri(String authUri) {
		this.authUri = authUri;
	}

	public void setTokenUri(String tokenUri) {
		this.tokenUri = tokenUri;
	}

	public void setAuthProvider_x509CertUrl(String authProvider_x509CertUrl) {
		this.authProvider_x509CertUrl = authProvider_x509CertUrl;
	}

	public void setClient_x509CertUrl(String client_x509CertUrl) {
		this.client_x509CertUrl = client_x509CertUrl;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public void setFlowControlSetting(boolean flowControlSetting) {
		this.flowControlSetting = flowControlSetting;
	}

	public void setParallelPullCount(String parallelPullCount) {
		if (Integer.parseInt(parallelPullCount) > 1) {
			this.parallelPullCount = parallelPullCount;
		} else {
			this.parallelPullCount = "1";
		}
	}

	public void setMaxOutstandingRequestBytes(String maxOutstandingRequestBytes) {
		if (maxOutstandingRequestBytes == null || maxOutstandingRequestBytes == "") {
			this.maxOutstandingRequestBytes = "1_000_000_000L";
		} else {
			this.maxOutstandingRequestBytes = maxOutstandingRequestBytes;
		}
	}

	public void setMaxAckExtensionPeriod(String maxAckExtensionPeriod) {
		this.maxAckExtensionPeriod = maxAckExtensionPeriod;
	}

	public void setMaxOutStandingElementCount(String maxOutStandingElementCount) {
		if (maxOutStandingElementCount == null || maxOutStandingElementCount == "") {
			this.maxOutStandingElementCount = "10_000L";
		} else {
			this.maxOutStandingElementCount = maxOutStandingElementCount;
		}
	}

	public static Subscriber getSubscriber() {
		return (Subscriber) JMeterContextService.getContext().getVariables().getObject(SUBSCRIBER_CONNECTION);
	}

	public static MessagesQueue getMessagesQueue() {
		return (MessagesQueue) JMeterContextService.getContext().getVariables().getObject(MESSAGESQUEUE);
	}
	
	public static String getSubscribedTopic() {
		return (String) JMeterContextService.getContext().getVariables().getObject(SUBSCRIBED_TOPIC);
	}

}
