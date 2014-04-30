package com.eternity.common.communication.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.eternity.common.message.Message;

public interface Decoder
{
  public Object decode(ByteBuffer data, Class<?> type) throws IOException;
}
