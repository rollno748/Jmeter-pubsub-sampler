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

