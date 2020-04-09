package com.di.jmeter.pubsub.config;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriberConfigBeanInfo extends BeanInfoSupport {
	private static Logger LOGGER = LoggerFactory.getLogger(SubscriberConfigBeanInfo.class);

	public SubscriberConfigBeanInfo() {
		super(SubscriberConfig.class);

		createPropertyGroup("credentials",
				new String[] { "type", "projectId", "topic", "subscriptionId", "privateKey", "privateKeyId", "tokenUri",
						"clientId", "clientEmail", "client_x509CertUrl", "authUri", "authProvider_x509CertUrl",
						"maxAckExtensionPeriod" });
		
		//FlowControl category
		createPropertyGroup("fcSettings", new String[] {"flowControlSetting", "parallelPullCount", "maxOutStandingElementCount", "maxOutstandingRequestBytes"});

		PropertyDescriptor p = property("projectId");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "<PROJECT ID>");
		
		p = property("subscriptionId");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "<SUBSCRIPTION ID>");
		
		p = property("type");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "service_account");

		p = property("topic");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "<TOPIC>");

		p = property("privateKey");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "<PRIVATE KEY>");

		p = property("privateKeyId");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "<PRIVATE KEY ID>");

		p = property("tokenUri");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "https://oauth2.googleapis.com/token");

		p = property("clientId");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "<CLIENT ID>");

		p = property("clientEmail");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "<CLIENT EMAIL>");

		p = property("client_x509CertUrl");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "<CLIENT CERT URL>");

		p = property("authUri");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "https://accounts.google.com/o/oauth2/auth");

		p = property("authProvider_x509CertUrl");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "https://www.googleapis.com/oauth2/v1/certs");

		p = property("maxAckExtensionPeriod");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "1");
		
		p = property("flowControlSetting");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, Boolean.FALSE);
		
		p = property("parallelPullCount");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "1");

		p = property("maxOutStandingElementCount");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "10_000L");

		p = property("maxOutstandingRequestBytes");
		p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(DEFAULT, "1_000_000_000L");


		if (LOGGER.isDebugEnabled()) {
			String subDescriptorsAsString = Arrays.stream(getPropertyDescriptors())
					.map(pd -> pd.getName() + "=" + pd.getDisplayName()).collect(Collectors.joining(" ,"));
			LOGGER.debug(subDescriptorsAsString);
		}
	}
}
