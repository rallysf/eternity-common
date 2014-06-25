package com.eternity.common.communication.protocol.protobuf.gson;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
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
      @SuppressWarnings("unchecked")
      Class<? extends GeneratedMessage> type = (Class<? extends GeneratedMessage>)typeToken;
      
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
        if(!obj.has(fieldName) || obj.get(fieldName).isJsonNull()) continue;
        
        if(field.isRepeated())
        {
          if(obj.get(fieldName).isJsonArray())
          {
            List<Object> list = Lists.newLinkedList();
            for(JsonElement e : obj.get(fieldName).getAsJsonArray())
            {
              list.add(deserializeField(context, e, field));
            }
            builder.setField(field, list);
          }
          else
          {
            builder.setField(field,
                             Lists.newArrayList(deserializeField(context,
                                                                 obj.get(fieldName),
                                                                 field)));
          }
        }
        else
        {
          builder.setField(field, deserializeField(context,
                                                   obj.get(fieldName),
                                                   field));
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

  private Object deserializeField(JsonDeserializationContext context,
                                JsonElement obj,
                                FieldDescriptor field)
                                    throws ClassNotFoundException
  {
    switch(field.getJavaType())
    {
    case BOOLEAN:
      return context.deserialize(obj,
                                 Boolean.TYPE);
    case BYTE_STRING:
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      JsonArray array = obj.getAsJsonArray();
      for(JsonElement e : array)
      {
        b.write(e.getAsInt());
      }
      return ByteString.copyFrom(b.toByteArray());
    case DOUBLE:
      return context.deserialize(obj,
                                 Double.TYPE);
    case ENUM:
      return field.getEnumType()
          .findValueByName(obj
                           .getAsString());
    case FLOAT:
      return context.deserialize(obj,
                                 Float.TYPE);
    case INT:
      return context.deserialize(obj,
                                 Integer.TYPE);
    case LONG:
      return context.deserialize(obj,
                                 Long.TYPE);
    case MESSAGE:
      String className = getClassName(field);
      Class<? extends GeneratedMessage> sub = Class
          .forName(className)
          .asSubclass(GeneratedMessage.class);
      return context.deserialize(obj,
                                 sub);
    case STRING:
      return context.deserialize(obj,
                                 String.class);
    default:
      return null;
    }
  }

  private String getClassName(FieldDescriptor field)
  {
	  FileDescriptor file = field.getMessageType().getFile();
	  FileOptions opts = file.getOptions();
	  StringBuilder classNameBuilder = new StringBuilder();
	  classNameBuilder.append(opts.getJavaPackage());
	  classNameBuilder.append(".");
	  if(!opts.getJavaMultipleFiles())
	  {
		  classNameBuilder.append(opts.getJavaOuterClassname());
		  classNameBuilder.append("$");
	  }
	  classNameBuilder.append(field.getMessageType().getName());
	  return classNameBuilder.toString();
  }
}
