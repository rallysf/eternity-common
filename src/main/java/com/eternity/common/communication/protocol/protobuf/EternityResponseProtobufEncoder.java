package com.eternity.common.communication.protocol.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.eternity.common.communication.protobuf.PROTO;
import com.eternity.common.communication.protocol.Encoder;
import com.eternity.common.message.Response;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;

public class EternityResponseProtobufEncoder
implements Encoder
{

  @Override
  public ByteBuffer encode(Object r) throws IOException
  {
    if(!(r instanceof Response)) throw new IOException("Invalid type!");
    
    Response response = (Response)r;
    PROTO.Response.Builder builder = PROTO.Response.newBuilder()
        .setStatus(response.getStatus());
    if(response.getStatus() == 200)
    {
        builder.setResponse(ByteString.copyFrom(((GeneratedMessage)response.data)
                                                .toByteArray()));
    }
    
    for(String error : response.errors)
    {
      if(error != null)
        builder.addError(error);
    }
    
    return ByteBuffer.wrap(builder.build().toByteArray());
  }

}
