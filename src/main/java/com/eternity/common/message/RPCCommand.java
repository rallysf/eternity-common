package com.eternity.common.message;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.eternity.common.communication.protocol.Decoder;
import com.eternity.common.communication.protocol.Encoder;
import com.eternity.common.exceptions.EternityException;

public abstract class RPCCommand<Input, Output> implements Command {

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
	  if(param == null)
	  {
	    return encoder.encode(execute(null));
	  }
	  return encoder.encode(execute(argType.cast(decoder.decode(param,
	                                                            argType))));
	}
	
	public abstract Output execute(Input arg) throws EternityException, Exception;
}
