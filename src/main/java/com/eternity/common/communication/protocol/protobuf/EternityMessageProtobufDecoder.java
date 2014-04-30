package com.eternity.common.communication.protocol.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import com.eternity.common.communication.protobuf.PROTO;
import com.eternity.common.communication.protobuf.PROTO.RequestParameter;
import com.eternity.common.communication.protocol.Decoder;
import com.eternity.common.message.Message;
import com.eternity.common.message.ParameterNames;
import com.google.protobuf.ByteString;

public class EternityMessageProtobufDecoder implements Decoder
{
  @SuppressWarnings("rawtypes")
  Class<? extends Enum> paramType;
  
  @SuppressWarnings("rawtypes")
  public EternityMessageProtobufDecoder(Class<? extends Enum> paramType)
  {
    this.paramType = paramType;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object decode(ByteBuffer data, Class<?> type) throws IOException
  {
    if(type != Message.class) throw new IOException("Invalid type!");
    
    PROTO.Request req = PROTO.Request.parseFrom(ByteString.copyFrom(data.array(),
                                                                    data.position(),
                                                                    data.limit()));
    Message msg = new Message();
    msg.body = ByteBuffer.wrap(req.getData().toByteArray());
    msg.commandName = req.getMethod();
    msg.subsystemName = req.getService();
    msg.encoding = "application/protobuf";

    msg.paramMap = new HashMap<ParameterNames, String>();
    for(RequestParameter k : req.getParametersList())
    {
      msg.paramMap.put((ParameterNames)Enum.valueOf(paramType, k.getName()),
                       k.getData());
    }
    return msg;
  }

}
