package com.eternity.common.communication.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Encoder
{
  public ByteBuffer encode(Object r);
}
