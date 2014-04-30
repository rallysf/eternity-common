package com.eternity.common.communication.protocol.json;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

import com.eternity.common.message.Message;
import com.eternity.common.message.ParameterNames;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class EternityMessageJsonDeserializer implements
    JsonDeserializer<Message>
{
  private final Type paramMapType = new TypeToken<Map<ParameterNames,String>>(){}.getType();
  
  private Charset charset;
  public EternityMessageJsonDeserializer()
  {
    charset = Charset.forName("UTF-8");
  }
  
  public EternityMessageJsonDeserializer(Charset charset)
  {
    this.charset = charset;
  }

  @Override
  public Message deserialize(JsonElement data,
                             Type type,
                             JsonDeserializationContext context)
                                 throws JsonParseException
  {
    if(!data.isJsonObject())
      throw new JsonParseException("Message must be an object!");
    
    JsonObject obj = data.getAsJsonObject();
    Message message = new Message();
    
    if(obj.has("commandName"))
    {
      message.commandName = obj.get("commandName").getAsString();
    }
    
    if(obj.has("subsystemName"))
    {
      message.subsystemName = obj.get("subsystemName").getAsString();
    }
    
    if(obj.has("paramMap"))
    {
      message.paramMap = context.deserialize(obj.get("paramMap"),
                                             paramMapType);
    }
    
    if(obj.has("body"))
    {
      JsonElement bodyElement = obj.get("body");
      if(bodyElement.isJsonObject())
      {
        message.body = ByteBuffer.wrap(bodyElement.toString().getBytes(charset));
        message.encoding = "application/json;" + charset.displayName();
      }
      else if(bodyElement.isJsonArray())
      {
        if(!obj.has("encoding"))
          throw new JsonParseException("Encoding required for array body!");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for(JsonElement e : bodyElement.getAsJsonArray())
        {
          baos.write(e.getAsByte());
        }
        message.encoding = context.deserialize(obj.get("encoding"),
                                               String.class);
        message.body = ByteBuffer.wrap(baos.toByteArray());
      }
      else throw new JsonParseException("Cannot parse message body!");
    }
    
    return message;
  }

}
