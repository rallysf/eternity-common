package com.eternity.common.test;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.junit.Test;

import com.eternity.common.SubSystemNames;
import com.eternity.common.communication.protocol.ProtocolHandlers;
import com.eternity.common.communication.protocol.json.EternityJSONProtocol;
import com.eternity.common.communication.protocol.json.EternityMessageJsonDeserializer;
import com.eternity.common.communication.protocol.protobuf.gson.GeneratedMessageJsonDeserializer;
import com.eternity.common.communication.protocol.protobuf.gson.GeneratedMessageJsonSerializer;
import com.eternity.common.message.Message;
import com.eternity.common.message.MessageConsumer;
import com.eternity.common.message.MessageConsumerFactory;
import com.eternity.common.message.ParameterNames;
import com.eternity.common.message.Response;
import com.eternity.reference.ReferenceMessageConsumer;
import com.eternity.reference.json.GsonFactory;
import com.eternity.reference.json.ParameterNamesJsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.GeneratedMessage;

public class TestWorkbench implements MessageConsumerFactory
{
  // private static Logger log = LoggerFactory.getLogger(TestWorkbench.class);

  @Test
  public void testValidFieldResponse() throws UnsupportedEncodingException,
                                      IOException
  {
    Gson gson = new GsonBuilder().registerTypeAdapter(GeneratedMessage.class,
                                                      new GeneratedMessageJsonSerializer())
                                 .registerTypeAdapter(GeneratedMessage.class,
                                                      new GeneratedMessageJsonDeserializer())
                                 .registerTypeAdapter(ParameterNames.class,
                                                      new ParameterNamesJsonDeserializer())
                                 .registerTypeAdapter(Message.class,
                                                      new EternityMessageJsonDeserializer())
                                 .create();
    EternityJSONProtocol protocol = new EternityJSONProtocol(
                                                             gson,
                                                             Charset.forName("UTF-8"));
    ProtocolHandlers.addDecoder("application/json;UTF-8", protocol);
    ProtocolHandlers.addEncoder("application/json;UTF-8", protocol);

    MessageConsumer mc = MessageConsumer.getInstance(TestSubSystems.alpha, this);
    mc.setReady(true);
    String message = "{\"commandName\":\"HelloWorld\",\"paramMap\": {\"first\":\"1234\",\"second\":\"1\",\"third\":\"12\"}, \"subsystemName\": \"alpha\",\"body\":{\"message\":\"bar\"}}";
    Message decodedMessage = (Message) ProtocolHandlers.getHandlers()
                                                       .getDecoder("application/json;UTF-8")
                                                       .decode(ByteBuffer.wrap(message.getBytes("UTF-8")),
                                                               Message.class);
    decodedMessage.encoding = "application/json;UTF-8";
    Response response = mc.processMessage(decodedMessage);
    assertEquals(200, response.getStatus());
    assertTrue(ByteBuffer.class.isAssignableFrom(response.data.getClass()));
  }

  // test json response

  // test that we get error when both are set

  // check bad parameter returns response object, and 400 w/correct error
  // message

  @Override
  public MessageConsumer createMessageConsumer(SubSystemNames subsystem)
  {

    return new ReferenceMessageConsumer(subsystem,
                                        "com.eternity.reference.commands");
  }

  @Override
  public Gson createGson()
  {
    return GsonFactory.getGson();
  }

}
