package com.eternity.common.communication.protobuf;

import com.google.protobuf.Message;

public interface EncodingStrategy<T>
{
	public T encode(Message message);
}
