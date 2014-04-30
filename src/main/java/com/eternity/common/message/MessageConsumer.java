package com.eternity.common.message;

/*
 The MIT License (MIT)

 Copyright (c) 2011 Sonjaya Tandon

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE. *
 */

import static org.reflections.util.ClasspathHelper.forPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eternity.common.SubSystemNames;
import com.eternity.common.annotations.BindCommand;
import com.eternity.common.communication.AsyncThreadPool;
import com.eternity.common.communication.protocol.Decoder;
import com.eternity.common.communication.protocol.Encoder;
import com.eternity.common.communication.protocol.ProtocolHandlers;
import com.eternity.common.exceptions.EternityException;
import com.google.gson.Gson;
import com.google.inject.Injector;

public abstract class MessageConsumer
{

  // /////////////////////////////////////////////////////////////////
  // flyweight support fields and methods
  // There will be an instance of a concrete version of this class/gameId
  // These methods are used to access those instances
  private static final HashMap<SubSystemNames, MessageConsumer> instances = new HashMap<SubSystemNames, MessageConsumer>();
  protected static SubSystemNames subsystemNames;

  // this method should only be used for testing

  public static final void resetForTesting(SubSystemNames subsystem)
  {
    instances.remove(subsystem);
  }

  public static final MessageConsumer getInstance(SubSystemNames subsystem,
                                                  MessageConsumerFactory messageConsumerFactory)
  {
    MessageConsumer instance = instances.get(subsystem);
    if (instance == null)
    {
      instance = createInstance(subsystem, messageConsumerFactory);
    }
    return instance;
  }

  public static final SubSystemNames getSubSystem(String subSystemId)
  {
    return subsystemNames.getSubSystem(subSystemId);
  }

  public static final void setSubSystemNames(SubSystemNames subsystemNames)
  {
    MessageConsumer.subsystemNames = subsystemNames;
  }

  public static final MessageConsumer lookup(SubSystemNames subsystem)
  {
    return instances.get(subsystem);
  }
  
  public static Response dispatchMessage(Message msg,
                                           MessageConsumerFactory factory,
                                           String hostName)
  {
    MessageConsumer consumer = MessageConsumer.getInstance(getSubSystem(msg.subsystemName),
                                                           factory);
    return consumer.processMessage(msg);
  }

  private static synchronized MessageConsumer createInstance(SubSystemNames subsystem,
                                                             MessageConsumerFactory messageConsumerFactory)
  {
    MessageConsumer instance = instances.get(subsystem);
    if (instance == null)
    {
      instance = messageConsumerFactory.createMessageConsumer(subsystem);
      instance.init();
      MessageConsumer oldInstance = instances.put(subsystem, instance);
      if (oldInstance != null)
      {
        instance = oldInstance;
      }
    }
    return instance;
  }

  // /////////////////////////////////////////////////////////////////
  // Base message consumer fields and methods
  protected Gson gson;
  protected SubSystemNames subsystem;
  protected RequestFactory requestFactory;
  protected Map<String, Command> commandRegistry = new HashMap<String, Command>();
  private boolean ready = false;
  private static Logger log = LoggerFactory.getLogger(MessageConsumer.class);
  private String commandsPackage = null;
  private Injector injector = null;

  protected MessageConsumer(SubSystemNames subsystem, String commandsPackage)
  {
    this.subsystem = subsystem;
    this.commandsPackage = commandsPackage;
  }

  public MessageConsumer(SubSystemNames subsystem, String commandsPackage,
      Injector injector)
  {
    this(subsystem, commandsPackage);
    this.injector = injector;
  }

  /**
   * populate the commandRegistry define the request and response factories
   */
  protected void init()
  {
    Reflections ref = new Reflections(new ConfigurationBuilder().setUrls(forPackage(commandsPackage)));
    Set<Class<?>> commands = ref.getTypesAnnotatedWith(BindCommand.class);

    for (Class<?> command : commands)
    {
      BindCommand binding = command.getAnnotation(BindCommand.class);
      Class<? extends Command> typedCommandClass = command.asSubclass(Command.class);
      try
      {
        Command cmd = null;
        if (injector != null)
        {
          cmd = injector.getInstance(typedCommandClass);
        }
        else
        {
          cmd = typedCommandClass.newInstance();
        }
        if (binding.value().equals(""))
        {
          commandRegistry.put(command.getSimpleName(), cmd);
        }
        else
        {
          commandRegistry.put(binding.value(), cmd);
        }
      }
      catch (InstantiationException e)
      {
        log.error("Unable to instatiate command: " + command.getName(), e);
      }
      catch (IllegalAccessException e)
      {
        log.error("Unable to instatiate command: " + command.getName(), e);
      }
    }
    setReady(true);
  }

  public boolean isReady()
  {
    return ready;
  }

  public void setReady(boolean ready)
  {
    this.ready = ready;
  }

  public Gson getGson()
  {
    return gson;
  }

  public Response processMessage(Message message)
  {
    log.debug(message.toString());
    Command command = commandRegistry.get(message.commandName);
    Response response = new Response();
    try
    {
      response.data = command.execute(message.body,
                                      getDecoderFor(message),
                                      getEncoderFor(message));
    }
    catch (IOException e)
    {
      response.setStatusToBadRequest_400();
      response.addError(e.getLocalizedMessage());
    }
    catch (EternityException e)
    {
      response.setStatus(e.getHttpStatusEquivalent());
      response.addError(e.getMessage());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      response.setStatus(500);
      response.addError(e.getMessage());
    }
    return response;
  }

  protected Decoder getDecoderFor(Message message)
  {
    return ProtocolHandlers.getHandlers().getDecoder(message.encoding);
  }

  protected Encoder getEncoderFor(Message message)
  {
    return ProtocolHandlers.getHandlers().getEncoder(message.encoding);
  }

  public List<Response> processMessages(ArrayList<Message> messages)
  {
    List<Response> result = new LinkedList<Response>();
    for (Message message : messages)
    {
      try
      {
        Response response = processMessage(message);
        result.add(response);
      }
      catch (Exception e)
      {
        // TODO: Something
      }
    }
    return result;
  }

  public static void dispatchAsyncMessage(Message msg,
                                            MessageConsumerFactory factory,
                                            String hostName)
  {
    MessageConsumer consumer = MessageConsumer.getInstance(getSubSystem(msg.subsystemName),
                                                           factory);
    AsyncThreadPool.instance.execute(consumer, msg);
  }
}
