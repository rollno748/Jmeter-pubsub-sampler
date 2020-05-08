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

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.gson.JsonObject;
import com.google.pubsub.v1.ProjectTopicName;

public class PublisherConfig extends ConfigTestElement implements Serializable, ConfigElement, TestStateListener, TestBean {

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

	private static final String PUBSUB_CONNECTION = "publisherConnection";

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

		if (variables.getObject(PUBSUB_CONNECTION) != null) {
			LOGGER.error("PubSub connection is already established and active !!");
		} else {
			synchronized (this) {
				try {
					publisherClient = Publisher.newBuilder(getGcpTopic())
							.setCredentialsProvider(createCredentialsProviderUsingJson(getCredentials())).build();

					variables.putObject(PUBSUB_CONNECTION, publisherClient);
					LOGGER.info(String.format("Publisher connection established with the %s successfully !!", getTopic()));
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
				publisherClient = null;
				LOGGER.info("Publisher connection Terminated successfully !!");
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

	public static Publisher getPublisherClient() {
		return (Publisher) JMeterContextService.getContext().getVariables().getObject(PUBSUB_CONNECTION);
	}

}
