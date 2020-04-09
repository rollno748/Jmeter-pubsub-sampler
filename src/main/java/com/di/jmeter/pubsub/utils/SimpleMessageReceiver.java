package com.di.jmeter.pubsub.utils;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;

public class SimpleMessageReceiver implements MessageReceiver {
    private final MessagesQueue messagesQueue;

    public SimpleMessageReceiver(final MessagesQueue messagesQueue) {
        this.messagesQueue = messagesQueue;
    }

    @Override
    public void receiveMessage(final PubsubMessage message, final AckReplyConsumer consumer) {
//    	System.out.println("Id : " + message.getMessageId());
//      System.out.println("Data : " + message.getData().toStringUtf8());
        
        if(messagesQueue.offer(message)){
            consumer.ack();
        }
        else{
            consumer.nack();
        }
    }

}
