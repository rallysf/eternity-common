package com.eternity.common.communication.protocol;

import java.util.HashMap;
import java.util.Map;

public class ProtocolHandlers
{
  private static ProtocolHandlers instance;
  
  private Map<String, Encoder> encoders;
  private Map<String, Decoder> decoders;
  
  private ProtocolHandlers()
  {
    encoders = new HashMap<String, Encoder>();
    decoders = new HashMap<String, Decoder>();
  }
  
  public static synchronized ProtocolHandlers getHandlers()
  {
    if(instance == null)
    {
      instance = new ProtocolHandlers();
    }
    return instance;
  }
  
  public void setEncoder(String type, Encoder encoder)
  {
    encoders.put(type, encoder);
  }
  
  public void setDecoder(String type, Decoder decoder)
  {
    decoders.put(type, decoder);
  }
  
  public Encoder getEncoder(String type)
  {
    return encoders.get(type);
  }
  
  public Decoder getDecoder(String type)
  {
    return decoders.get(type);
  }
  
  public static void addEncoder(String type, Encoder encoder)
  {
    getHandlers().setEncoder(type, encoder);
  }
  
  public static void addDecoder(String type, Decoder decoder)
  {
    getHandlers().setDecoder(type, decoder);
  }
}
