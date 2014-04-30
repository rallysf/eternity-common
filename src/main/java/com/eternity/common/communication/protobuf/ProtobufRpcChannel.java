package com.eternity.common.communication.protobuf;

import java.util.concurrent.ExecutorService;

import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcChannel;
import com.google.protobuf.RpcController;
import com.google.protobuf.Service;

public abstract class ProtobufRpcChannel<T> implements RpcChannel
{
	protected EncodingStrategy<T> encoder;
	protected DecodingStrategy<T> decoder;
	protected Service service;
	public ProtobufRpcChannel(EncodingStrategy<T> encoder,
			DecodingStrategy<T> decoder,
			Service service,
			ExecutorService executors)
	{
		this.encoder = encoder;
		this.decoder = decoder;
	}

	@Override
	public void callMethod(MethodDescriptor method,
			RpcController controller,
			Message request,
			Message response,
			RpcCallback<Message> callback)
	{
	}
}
