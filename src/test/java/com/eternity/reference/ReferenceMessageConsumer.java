package com.eternity.reference;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eternity.common.SubSystemNames;
import com.eternity.common.message.MessageConsumer;
import com.eternity.common.message.ParameterNames;
import com.eternity.common.message.Request;
import com.eternity.common.message.RequestFactory;
import com.eternity.common.test.TestSubSystems;
import com.eternity.reference.json.GsonFactory;

public class ReferenceMessageConsumer extends MessageConsumer implements RequestFactory {

	private static Logger log = LoggerFactory.getLogger(ReferenceMessageConsumer.class);

	public ReferenceMessageConsumer(SubSystemNames subsystem, String commandPackage) {
		super(subsystem, commandPackage);
		requestFactory = this;
		gson = GsonFactory.getGson();
		subsystemNames = TestSubSystems.alpha;  // any instance will do
	}

	@Override
	public Request createRequest(Map<ParameterNames, String> paramMap) {
		return new ReferenceRequest(paramMap, gson);
	}
}
