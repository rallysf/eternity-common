package com.eternity.common.communication.protobuf;

import com.google.protobuf.Message;

public interface DecodingStrategy<T>
{
	public Message decode(T data);
}
