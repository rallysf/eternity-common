package com.eternity.common.annotations;

import com.eternity.common.message.MessageNames;

public @interface BindCommand {
	Class<? extends Enum<? extends MessageNames>> clazz();
	String name();
}
