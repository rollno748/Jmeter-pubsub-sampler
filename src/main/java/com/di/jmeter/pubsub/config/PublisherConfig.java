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
import java.util.concurrent.TimeUnit;

import com.google.api.gax.batching.BatchingSettings;
import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.TestBeanHelper;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.JsonObject;
import com.google.pubsub.v1.ProjectTopicName;
import org.threeten.bp.Duration;

/**
 * @author Mohamed Ibrahim
 *
 */
public class PublisherConfig extends ConfigTestElement
		implements ConfigElement, TestStateListener, TestBean, Serializable {

	private static Logger LOGGER = LoggerFactory.getLogger(PublisherConfig.class);
	private static final long serialVersionUID = 7645049205276507368L;

	private Publisher publisherClient;
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

	private String publisherConnection;
	private String batchingEnabled;
	private String batchingElementCountThreshold;
	private String batchingRequestByteThreshold;
	private String batchingDelayThreshold;

	// Default Constructor
	public PublisherConfig() {

	}

	@Override
	public void addConfigElement(ConfigElement config) {

	}

	@Override
	public boolean expectsModification() {
		return false;
	}

	@Override
	public void testStarted() {
		this.setRunningVersion(true);
		TestBeanHelper.prepare(this);
		JMeterVariables variables = getThreadContext().getVariables();

		if (variables.getObject(publisherConnection) != null) {
			LOGGER.error("PubSub connection is already established and active !!");
		} else {
			synchronized (this) {
				try {
					Boolean isBatchingEnabled = Boolean.parseBoolean(getBatchingEnabled());
					BatchingSettings.Builder batchingSettingsBuilder = BatchingSettings.newBuilder()
							.setIsEnabled(isBatchingEnabled);
					if (Boolean.TRUE.equals(isBatchingEnabled)) {
						batchingSettingsBuilder.setElementCountThreshold(Long.parseLong(getBatchingElementCountThreshold()))
								.setRequestByteThreshold(Long.parseLong(getBatchingRequestByteThreshold()))
								.setDelayThreshold(Duration.ofMillis(Long.parseLong(getBatchingDelayThreshold())));
					}

					publisherClient = Publisher.newBuilder(getGcpTopic())
							.setBatchingSettings(batchingSettingsBuilder.build())
							.setCredentialsProvider(createCredentialsProviderUsingJson(getCredentials())).build();

					variables.putObject(publisherConnection, publisherClient);
					LOGGER.info(
							String.format("Publisher connection established with the %s successfully !!", getTopic()));
				} catch (NumberFormatException e) {
					LOGGER.error("Exception occurred while parsing publisher properties, check batching and other values specified : ", e);
					e.printStackTrace();
				} catch (IOException e) {
					LOGGER.error("Exception Occured while establishing connection with GCP : ");
					e.printStackTrace();
				}
			}
		}
	}

	private ProjectTopicName getGcpTopic() {
		return ProjectTopicName.of(getProjectId(), getTopic());
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

	@Override
	public void testStarted(String host) {
		testStarted();
	}

	@Override
	public void testEnded() {
		synchronized (this) {
			if (publisherClient != null) {
				publisherClient.shutdown();
				try {
					publisherClient.awaitTermination(30, TimeUnit.SECONDS);
					LOGGER.info("Publisher connection Terminated successfully !!");
				} catch (InterruptedException e) {
					LOGGER.info("Error occurred while terminating Publisher connection");
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void testEnded(String host) {
		testEnded();
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

	// =============== > Getters and setters < ================

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

	public String getBatchingEnabled() {
		return batchingEnabled;
	}

	public String getBatchingElementCountThreshold() {
		return batchingElementCountThreshold;
	}

	public String getBatchingRequestByteThreshold() {
		return batchingRequestByteThreshold;
	}

	public String getBatchingDelayThreshold() {
		return batchingDelayThreshold;
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

	public String getPublisherConnection() {
		return publisherConnection;
	}

	public void setPublisherConnection(String publisherConnection) {
		this.publisherConnection = publisherConnection;
	}

	public void setBatchingEnabled(String batchingEnabled) {
		this.batchingEnabled = batchingEnabled;
	}

	public void setBatchingElementCountThreshold(String batchingElementCountThreshold) {
		this.batchingElementCountThreshold = batchingElementCountThreshold;
	}

	public void setBatchingRequestByteThreshold(String batchingRequestByteThreshold) {
		this.batchingRequestByteThreshold = batchingRequestByteThreshold;
	}

	public void setBatchingDelayThreshold(String batchingDelayThreshold) {
		this.batchingDelayThreshold = batchingDelayThreshold;
	}

}
