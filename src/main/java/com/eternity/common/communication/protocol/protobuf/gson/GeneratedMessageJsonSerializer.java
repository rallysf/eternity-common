package com.eternity.common.communication.protocol.protobuf.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;

public class GeneratedMessageJsonSerializer implements
    JsonSerializer<GeneratedMessage>
{

  @Override
  public JsonElement serialize(GeneratedMessage message,
                               Type type,
                               JsonSerializationContext context)
  {
    JsonObject obj = new JsonObject();
    for(FieldDescriptor field : message.getDescriptorForType().getFields())
    {
      obj.add(field.getName(),
              context.serialize(message.getField(field)));
    }
    return obj;
  }
}
