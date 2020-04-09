# Jmeter-GCP-PubSub-Sampler

## Introduction

This plugin adds feature to connect to a topic to publish/subscribe messages in GCP

## Required Components

1. Jmeter
2. GCP Pubsub configs


## Jar Dependencies Required

* google-cloud-pubsub-1.88.0.jar or above
* gson-2.2.4.jar

## Jmeter Target

* Jmeter version 5.1.1 or above
* Java 8 or above

## Installation Instructions

* Download the source code from the Gitlab.
* Just do a mvn clean install (Git bash is required)
* Jar will be generated under the target directory (jmeter-pubsub-sampler-1.0.jar)
* Copy the Jar to \<Jmeter Installed Directory\>/lib/ext/

## How to use it
Add required config element (Publisher config/ Subscriber config)

* Provide the required credentials information in the Publisher config/ Subscriber config for the project
* To publish message, Add Publisher sampler to TG and then pass the message to publish
* To receive message, Add Subscriber sampler to TG to read the mesasges

# Publisher Info
Apart from config element, Publisher sampler has Gzip compression feature
* The Flag in the sampler will allow the mechanism to publish the message with/without Gzip compression 
* On successfull publish of each message, GCP returns a unique Id which will be returned in response header.

# Subscriber Info
The subscriber works perfectly. The current mechanism of Subscriber is 
* The config elements creats a local queue to store the messages received from the GCP.
* Currently the Queue size is hardcoded to 100K 
* The Subscriber sampler is bound to read it form the queue, which is already subscribed by the Message receiver(which is a separate client library's thread)
* The subscriber sampler is reading and removing the message from the queue. In future, will change the ack to be done by the sampler.
* When the susbcriber sampler is triggered and If there is no messages in the queue, It will wait until a new message arrive to process it.
* each message retrieved from the subscriber will have the message ID and publish time info along with the message.
* The message ID and publish time info will be returned in response headers and message on response body
* The subscriber sampler supports De-Compression, If the message is in gzip compressed format.


## References

Below are the references which guided to build this plugin.

* https://cloud.google.com/pubsub/docs/quickstart-client-libraries
* https://cloud.google.com/pubsub/docs/publisher
* https://cloud.google.com/pubsub/docs/pull
