package com.eternity.common.communication.protobuf;

import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;

public class ProtobufMessageGsonAdapter implements JsonDeserializer<Message>,
    JsonSerializer<Message>
{

  @Override
  public JsonElement serialize(Message message,
                               java.lang.reflect.Type type,
                               JsonSerializationContext context)
  {
    JsonObject result = new JsonObject();
    
    Map<FieldDescriptor, Object> fields = message.getAllFields();
    for(FieldDescriptor f : fields.keySet())
    {
      result.add(f.getName(), context.serialize(fields.get(f)));
    }
    
    return result;
  }

  @Override
  public Message deserialize(JsonElement message,
                             java.lang.reflect.Type type,
                             JsonDeserializationContext context)
                                 throws JsonParseException
  {
    return null;
  }

}
