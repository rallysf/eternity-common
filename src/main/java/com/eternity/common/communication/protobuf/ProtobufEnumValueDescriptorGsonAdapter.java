package com.eternity.common.communication.protobuf;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.EnumValueDescriptor;

public class ProtobufEnumValueDescriptorGsonAdapter implements
    JsonDeserializer<Descriptors.EnumValueDescriptor>, JsonSerializer<Descriptors.EnumValueDescriptor>
{

  @Override
  public JsonElement serialize(EnumValueDescriptor value,
                               Type type,
                               JsonSerializationContext context)
  {
    return new JsonPrimitive(value.getFullName());
  }

  @Override
  public EnumValueDescriptor deserialize(JsonElement data,
                                         Type type,
                                         JsonDeserializationContext context)
                                             throws JsonParseException
  {
    // TODO Auto-generated method stub
    return null;
  }

}
