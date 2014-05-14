package com.eternity.common.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eternity.common.communication.protocol.Decoder;
import com.eternity.common.communication.protocol.Encoder;
import com.eternity.common.exceptions.EternityException;

public abstract class RPCCommand<Input, Output> implements Command {
  private static final Logger log = LoggerFactory.getLogger(RPCCommand.class);

	protected Class<Input> argType;
	protected Class<Output> respType;
	
	public RPCCommand(Class<Input> argType, Class<Output> respType) {
		this.argType = argType;
		this.respType = respType;
	}

	@Override
	public ByteBuffer execute(ByteBuffer param,
	                          Decoder decoder,
	                          Encoder encoder) throws IOException, EternityException, Exception
	{
	  log.debug("Received command: {}", getClass().getName());
	  if(param == null)
	  {
	    return encoder.encode(execute(null));
	  }
	  return encoder.encode(execute(argType.cast(decoder.decode(param,
	                                                            argType))));
	}
	
	public abstract Output execute(Input arg) throws EternityException, Exception;
}
