package com.eternity.common.communication.zmq;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import com.eternity.common.communication.protocol.ProtocolHandlers;
import com.eternity.common.message.Message;
import com.eternity.common.message.MessageConsumer;
import com.eternity.common.message.Response;

public class EternityZMQTransport
implements Runnable
{
  private final static Logger log = LoggerFactory.getLogger(EternityZMQTransport.class);

  private int threadCount;
  private int workerCount;
  private String bindURI;
  private String encoding;
  private ZMQ.Context context;

  public EternityZMQTransport(int threadCount,
                              String bindURI,
                              int workerCount,
                              String encoding)
  {
    this.threadCount = threadCount;
    this.bindURI = bindURI;
    this.workerCount = workerCount;
    this.encoding = encoding;
  }
  
  public ZMQ.Context getContext()
  {
    return context;
  }

  public void run()
  {
    context = ZMQ.context(threadCount);
    ZMQ.Socket clients = context.socket(ZMQ.ROUTER);
    ZMQ.Socket workers = context.socket(ZMQ.DEALER);
    clients.bind(bindURI);

    String dispatchChannel = UUID.randomUUID().toString();
    log.debug("Using internal worker dispatch channel: " + dispatchChannel);

    workers.bind("inproc://" + dispatchChannel);

    List<Dispatcher> dispatchers = new LinkedList<Dispatcher>();
    
    for (int i = 0; i < workerCount; ++i)
    {
      Dispatcher dispatcher = new Dispatcher(dispatchChannel, context);
      dispatcher.start();
      dispatchers.add(dispatcher);
    }

    ZMQ.proxy(clients, workers, null);
    clients.close();
    workers.close();
    log.info("ZMQ transport layer shut down.");
  }

  public class Dispatcher extends Thread
  {
    private String channel;
    private ByteBuffer in;
    private ZMQ.Context context;

    public Dispatcher(String channel, ZMQ.Context context)
    {
      this.channel = channel;
      in = ByteBuffer.allocate(1048576);
      this.context = context;
    }

    public void run()
    {
      ZMQ.Socket socket = context.socket(ZMQ.REP);
      socket.connect("inproc://" + channel);

      try
      {
        while (true)
        {
          if (socket.recvByteBuffer(in, 0) < 1)
          {
            // TODO: Handle error
          }
          try
          {
            Message message = (Message)ProtocolHandlers.getHandlers()
                .getDecoder(encoding)
                .decode(ByteBuffer.wrap(in.array(), 0, in.position()),
                        Message.class);
            Response response = MessageConsumer.dispatchMessage(message, null, null);
            ByteBuffer out = ProtocolHandlers.getHandlers()
                .getEncoder(encoding)
                .encode(response);
            if(!socket.send(out.array(), 0))
              throw new IOException("Failed to send response!");
            in.clear();
          }
          catch (IOException e)
          {
            log.error("Critical error in SyncDispatch worker!", e);
          }
        }
      }
      catch(ZMQException e)
      {
        // this is okay because we're exiting
      }
      try
      {
        socket.close();
      }
      catch (ZMQException e)
      {
        //This is sometimes done for us when the context is terminated
      }
    }
  }
}
