package com.eternity.common.communication.protocol.protobuf.gson;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.FileOptions;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message.Builder;

public class GeneratedMessageJsonDeserializer implements
    JsonDeserializer<GeneratedMessage>
{
  private static final Logger log = LoggerFactory.getLogger(GeneratedMessageJsonDeserializer.class);

  @Override
  public GeneratedMessage deserialize(JsonElement json,
                                      Type typeToken,
                                      JsonDeserializationContext context)
                                          throws JsonParseException
  {
    try
    {
      Class<? extends GeneratedMessage> type = Class.forName(typeToken.toString())
          .asSubclass(GeneratedMessage.class);
      
      Builder builder = (Builder)type.getMethod("newBuilder")
          .invoke(null);
      
      if(!json.isJsonObject())
        return type.cast(builder.build());
      
      JsonObject obj = json.getAsJsonObject();
      
      Descriptor descriptor = (Descriptor)type.getMethod("getDescriptor")
          .invoke(null);
      for(FieldDescriptor field : descriptor.getFields())
      {
        String fieldName = field.getName();
        if(!obj.has(fieldName)) continue;
        
        switch(field.getJavaType())
        {
        case BOOLEAN:
          builder.setField(field,
                           context.deserialize(obj.get(fieldName),
                                               Boolean.TYPE));
          break;
        case BYTE_STRING:
          ByteArrayOutputStream b = new ByteArrayOutputStream();
          JsonArray array = obj.get(field.getName()).getAsJsonArray();
          for(JsonElement e : array)
          {
            b.write(e.getAsInt());
          }
          builder.setField(field, ByteString.copyFrom(b.toByteArray()));
          break;
        case DOUBLE:
          builder.setField(field,
                           context.deserialize(obj.get(fieldName),
                                               Double.TYPE));
          break;
        case ENUM:
          builder.setField(field,
                           field.getEnumType()
                                .findValueByName(obj.get(field.getName())
                                                    .getAsString()));
          break;
        case FLOAT:
          builder.setField(field,
                           context.deserialize(obj.get(fieldName),
                                               Float.TYPE));
          break;
        case INT:
          builder.setField(field,
                           context.deserialize(obj.get(fieldName),
                                               Integer.TYPE));
          break;
        case LONG:
          builder.setField(field,
                           context.deserialize(obj.get(fieldName),
                                               Long.TYPE));
          break;
        case MESSAGE:
          FileDescriptor file = field.getMessageType().getFile();
          FileOptions opts = file.getOptions();
          StringBuilder classNameBuilder = new StringBuilder();
          classNameBuilder.append(opts.getJavaPackage());
          classNameBuilder.append(".");
          if(!opts.getJavaMultipleFiles())
          {
            classNameBuilder.append(opts.getJavaOuterClassname());
            classNameBuilder.append(".");
          }
          classNameBuilder.append(field.getMessageType().getName());
          Class<? extends GeneratedMessage> sub = Class
              .forName(classNameBuilder.toString())
              .asSubclass(GeneratedMessage.class);
          builder.setField(field,
                           context.deserialize(obj.get(fieldName),
                                               sub));
          break;
        case STRING:
          builder.setField(field,
                           context.deserialize(obj.get(fieldName),
                                               String.class));
          break;
        default:
          break;
        
        }
      }
      
      return type.cast(builder.build());
    }
    catch (ClassNotFoundException e)
    {
      log.error(e.getMessage(), e);
    }
    catch (IllegalArgumentException e)
    {
      log.error(e.getMessage(), e);
    }
    catch (SecurityException e)
    {
      log.error(e.getMessage(), e);
    }
    catch (IllegalAccessException e)
    {
      log.error(e.getMessage(), e);
    }
    catch (InvocationTargetException e)
    {
      log.error(e.getMessage(), e);
    }
    catch (NoSuchMethodException e)
    {
      log.error(e.getMessage(), e);
    }
    return null;
  }
}
