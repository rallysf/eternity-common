package com.eternity.reference.commands;
import com.eternity.common.annotations.BindCommand;
import com.eternity.common.exceptions.EternityException;
import com.eternity.reference.RPCCommand;

@BindCommand
public class HelloWorld extends RPCCommand<HelloWorld.Request, HelloWorld.Response> {
  public class Request
  {
    String message;
  }
  
  public class Response
  {
    public String reply = "Hello world!";
  }
  
  public HelloWorld()
  {
    super(Request.class, Response.class);
  }

  @Override
  public Response execute(Request arg) throws EternityException, Exception
  {
    return new Response();
  }

}
