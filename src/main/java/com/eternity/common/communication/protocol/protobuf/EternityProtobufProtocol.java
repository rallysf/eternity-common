package com.eternity.common.communication.protocol.protobuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eternity.common.communication.protocol.Decoder;
import com.eternity.common.communication.protocol.Encoder;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;

public class EternityProtobufProtocol implements
    Encoder, Decoder
{
  private static final Logger log = LoggerFactory.getLogger(EternityProtobufProtocol.class);
  
  private Map<Class<?>, Decoder> typeDecoders = new HashMap<Class<?>, Decoder>();
  private Map<Class<?>, Encoder> typeEncoders = new HashMap<Class<?>, Encoder>();
  
  public void addTypeDecoder(Class<?> type, Decoder decoder)
  {
    typeDecoders.put(type, decoder);
  }
  
  public void addTypeEncoder(Class<?> type, Encoder encoder)
  {
    typeEncoders.put(type, encoder);
  }

  @Override
  public Object decode(ByteBuffer data, Class<?> argType)
      throws IOException
  {
    if(typeDecoders.containsKey(argType))
    {
      return typeDecoders.get(argType).decode(data, argType);
    }
    try
    {
      Method parser = argType.getMethod("parseFrom", byte[].class);
      return parser.invoke(null,
                           ByteString.copyFrom(data.array(),
                                               data.position(),
                                               data.limit()));
    }
    catch (SecurityException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (NoSuchMethodException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IllegalArgumentException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InvocationTargetException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public ByteBuffer encode(Object r) throws IOException
  {
    if(typeEncoders.containsKey(r.getClass()))
    {
      return typeEncoders.get(r.getClass()).encode(r);
    }
    GeneratedMessage message = (GeneratedMessage)r;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    message.writeTo(out);
    return ByteBuffer.wrap(out.toByteArray());
  }

}
