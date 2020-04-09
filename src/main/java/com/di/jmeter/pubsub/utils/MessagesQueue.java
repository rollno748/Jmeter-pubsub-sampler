package com.di.jmeter.pubsub.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.google.pubsub.v1.PubsubMessage;


public class MessagesQueue {
	
	
	private final BlockingQueue<PubsubMessage> messages;

    public MessagesQueue(int maxQueueSize){
        this.messages = new LinkedBlockingDeque<>(maxQueueSize);
    }

    protected MessagesQueue(BlockingQueue<PubsubMessage> messages){
        this.messages = messages;
    }

    public PubsubMessage take() throws InterruptedException {
        return messages.take();
    }
    
    public int getSize(){
        return messages.size();
    }

    public boolean offer(PubsubMessage message){
        return messages.offer(message);
    }
    

}

