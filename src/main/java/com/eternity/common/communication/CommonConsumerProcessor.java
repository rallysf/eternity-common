package com.eternity.common.communication;

/*
The MIT License (MIT)

Copyright (c) 2011 Sonjaya Tandon

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE. * 
 */


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.eternity.common.communication.protocol.ProtocolHandlers;
import com.eternity.common.message.Message;
import com.eternity.common.message.MessageConsumer;
import com.eternity.common.message.MessageConsumerFactory;
import com.eternity.common.message.Response;
import com.eternity.socket.server.ConsumerProcessor;
import com.google.gson.Gson;

public class CommonConsumerProcessor extends ConsumerProcessor {
	protected Gson gson;
	protected String hostName;
	protected MessageConsumerFactory messgeConsumerFactory;
	
	public CommonConsumerProcessor(String hostName, MessageConsumerFactory messgeConsumerFactory) {
		super();
		this.hostName = hostName;
		this.messgeConsumerFactory = messgeConsumerFactory;
		this.gson = messgeConsumerFactory.createGson();
	}
	
	@Override
	public String getResponse(String JSON) {
		String retVal = NO_RESPONSE;
		Message message = null;
		try
		{
		  message = (Message)ProtocolHandlers.getHandlers()
		      .getDecoder("application/json;UTF-8")
		      .decode(ByteBuffer.wrap(JSON.getBytes("UTF-8")),
		              Message.class);
		}
		catch (UnsupportedEncodingException e1)
		{
		  //Doesn't happen - guaranteed by JVM.
		}
		catch (IOException e1)
		{
		  //Doesn't happen - no real I/O.
		}
		
		log.debug("Message="+message);

		Response response = MessageConsumer.dispatchMessage(message, messgeConsumerFactory, hostName);
		try
		{
		  retVal = new String(ProtocolHandlers.getHandlers()
		                      .getEncoder("application/json;UTF-8")
		                      .encode(response)
		                      .array(),
		                      "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
		  //This doesn't happen as UTF-8 support is guaranteed by the JVM spec.
		}
		catch (IOException e)
		{
		  //This doesn't happen either as we're not doing any real I/O.
		}

		log.debug(retVal);
		return retVal;
	}


}
