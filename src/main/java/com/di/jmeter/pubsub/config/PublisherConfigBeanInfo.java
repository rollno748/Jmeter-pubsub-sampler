package com.di.jmeter.pubsub.config;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublisherConfigBeanInfo extends BeanInfoSupport {

	private static Logger LOGGER = LoggerFactory.getLogger(PublisherConfigBeanInfo.class);

	public PublisherConfigBeanInfo() {
		super(PublisherConfig.class);
		
		createPropertyGroup("credentials", new String[] { "type", "projectId", "topic", "privateKey", "privateKeyId", "tokenUri",  
				"clientId", "clientEmail", "client_x509CertUrl", "authUri", "authProvider_x509CertUrl" });

		PropertyDescriptor propertyDescriptor =  property("type");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "service_account");

		propertyDescriptor =  property("projectId");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "<PROJECT ID>");

		propertyDescriptor =  property("topic");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "<TOPIC>");

		propertyDescriptor =  property("privateKey");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "<PRIVATE KEY>");

		propertyDescriptor =  property("privateKeyId");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "<PRIVATE KEY ID>");

		propertyDescriptor =  property("tokenUri");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "https://oauth2.googleapis.com/token");

		propertyDescriptor =  property("clientId");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "<CLIENT ID>");

		propertyDescriptor =  property("clientEmail");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "<CLIENT EMAIL>");

		propertyDescriptor =  property("client_x509CertUrl");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "<CLIENT CERT URL>");

		propertyDescriptor =  property("authUri");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "https://accounts.google.com/o/oauth2/auth");

		propertyDescriptor =  property("authProvider_x509CertUrl");
		propertyDescriptor.setValue(NOT_UNDEFINED, Boolean.TRUE);
		propertyDescriptor.setValue(DEFAULT, "https://www.googleapis.com/oauth2/v1/certs");
		

		if (LOGGER.isDebugEnabled()) {
			String pubDescriptorsAsString = Arrays.stream(getPropertyDescriptors())
					.map(pd -> pd.getName() + "=" + pd.getDisplayName()).collect(Collectors.joining(" ,"));
			LOGGER.debug(pubDescriptorsAsString);
		}

	}

}
