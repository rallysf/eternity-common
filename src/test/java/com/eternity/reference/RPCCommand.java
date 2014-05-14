package com.eternity.reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eternity.common.message.Request;
import com.eternity.common.message.Response;


public abstract class RPCCommand<I, O> extends com.eternity.common.message.RPCCommand<I, O> {
	private static Logger log = LoggerFactory.getLogger(RPCCommand.class);
	
	public RPCCommand(Class<I> argType, Class<O> respType)
	{
	  super(argType, respType);
	}
	
	public boolean executeAlways(){
		return false;
	}

}
