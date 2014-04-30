package com.eternity.common.test;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.zeromq.ZMQ;

import com.eternity.common.SubSystemNames;
import com.eternity.common.communication.protobuf.PROTO;
import com.eternity.common.communication.protocol.ProtocolHandlers;
import com.eternity.common.communication.protocol.protobuf.EternityMessageProtobufDecoder;
import com.eternity.common.communication.protocol.protobuf.EternityProtobufProtocol;
import com.eternity.common.communication.protocol.protobuf.EternityResponseProtobufEncoder;
import com.eternity.common.communication.zmq.EternityZMQTransport;
import com.eternity.common.message.Message;
import com.eternity.common.message.MessageConsumer;
import com.eternity.common.message.MessageConsumerFactory;
import com.eternity.common.message.Response;
import com.eternity.reference.Parameters;
import com.eternity.reference.ReferenceMessageConsumer;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;

public class ProtoZMQMessageTest
{

  @Test
  public void test() throws Exception
  {
    MessageConsumer.getInstance(TestSubSystems.alpha,
                                new MessageConsumerFactory()
    {

      @Override
      public MessageConsumer createMessageConsumer(SubSystemNames subsystem)
      {
        return new ReferenceMessageConsumer(subsystem, "com.eternity.reference.commans");
      }

      @Override
      public Gson createGson()
      {
        // TODO Auto-generated method stub
        return null;
      }
      
    });
    EternityProtobufProtocol proto = new EternityProtobufProtocol();
    proto.addTypeDecoder(Message.class, new EternityMessageProtobufDecoder(Parameters.class));
    proto.addTypeEncoder(Response.class, new EternityResponseProtobufEncoder());
    ProtocolHandlers.addDecoder("application/protobuf",
                                proto);
    ProtocolHandlers.addEncoder("application/protobuf",
                                proto);
    EternityZMQTransport transport = new EternityZMQTransport(10,
                                                              "tcp://*:3101",
                                                              1,
                                                              "application/protobuf");
    Thread t = new Thread(transport);
    t.start();
    ZMQ.Context ctx = ZMQ.context(1);
    ZMQ.Socket socket = ctx.socket(ZMQ.REQ);
    
    PROTO.Request request = PROTO.Request.newBuilder()
        .setMethod("test1")
        .setService(TestSubSystems.alpha.name())
        .setData(ByteString.EMPTY)
        .build();
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    request.writeTo(out);

    Thread.sleep(5000);

    socket.connect("tcp://127.0.0.1:3101");

    socket.send(out.toByteArray(), 0);

    socket.recv();

    socket.close();
    ctx.term();
    transport.getContext().term();
  }
}
