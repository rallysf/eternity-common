package com.eternity.common.communication.protobuf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

public class JSONEncodingStrategy implements EncodingStrategy<String>
{

  @Override
  public String encode(Message message)
  {
    Gson gson = new GsonBuilder().registerTypeAdapter(Message.class,
                                                      new ProtobufMessageGsonAdapter())
                                 .registerTypeAdapter(Descriptors.EnumValueDescriptor.class,
                                                      new ProtobufEnumValueDescriptorGsonAdapter())
                                 .create();
    return gson.toJson(message);
  }

}
