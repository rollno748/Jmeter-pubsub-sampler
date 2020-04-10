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
