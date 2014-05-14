package com.eternity.common.communication.protocol.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.eternity.common.communication.protocol.Decoder;
import com.eternity.common.communication.protocol.Encoder;
import com.google.gson.Gson;

public class EternityJSONProtocol
implements Decoder, Encoder
{
  private Gson gson;
  private Charset charset;

  public EternityJSONProtocol(Gson gson, Charset charset)
  {
    this.gson = gson;
    this.charset = charset;
  }

  @Override
  public Object decode(ByteBuffer data, Class<?> type)
      throws IOException
  {
    return gson.fromJson(new String(data.array(),
                                    data.position(),
                                    data.limit(),
                                    charset), type);
  }

  @Override
  public ByteBuffer encode(Object r)
      throws IOException
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write(gson.toJson(r).getBytes(charset));
    return ByteBuffer.wrap(out.toByteArray());
  }
}
