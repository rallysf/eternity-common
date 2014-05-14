package com.eternity.common.communication.servlet;

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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eternity.common.communication.protocol.Decoder;
import com.eternity.common.communication.protocol.Encoder;
import com.eternity.common.communication.protocol.ProtocolHandlers;
import com.eternity.common.message.Message;
import com.eternity.common.message.MessageConsumer;
import com.eternity.common.message.MessageConsumerFactory;
import com.eternity.common.message.Parameter;
import com.eternity.common.message.Response;

@Deprecated
public abstract class SyncDispatch extends HttpServlet implements MessageConsumerFactory {
	private static final long serialVersionUID = 42L;
	private static Logger log = LoggerFactory.getLogger(SyncDispatch.class);
	private String hostName;

	public SyncDispatch() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName();
		} catch (UnknownHostException e) {
			log.error("", e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response, null);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response, request.getInputStream());
  }

  protected void doRequest(HttpServletRequest req, HttpServletResponse resp,
                           InputStream data) throws ServletException,
                                            IOException
  {
    String contentType = req.getContentType();
    String charset = req.getCharacterEncoding();
    String acceptsHeader = req.getHeader("Accept");
    Encoder encoder = null;
    
    if(charset == null)
    {
      charset = "UTF-8";
    }
    
    contentType += ";" + charset;
    
    Decoder decoder = ProtocolHandlers.getHandlers()
        .getDecoder(contentType);
    
    if(decoder == null)
    {
      resp.setStatus(400);
      PrintWriter p = resp.getWriter();
      p.write("Unacceptable Content-Type!");
      log.warn("Received request with invalid content type of: {} (derived: {})",
               req.getContentType(),
               contentType);
      return;
    }

    if (acceptsHeader != null)
    {
      String accepts[] = acceptsHeader.split(",");

      for (String accept : accepts)
      {
        encoder = ProtocolHandlers.getHandlers()
            .getEncoder(accept.trim() +";" + charset);
        if (encoder != null)
          break;
      }
    }
    else
    {
      encoder = ProtocolHandlers.getHandlers().getEncoder(contentType);
    }

    if (encoder == null)
    {
      resp.setStatus(400);
      PrintWriter p = resp.getWriter();
      p.println("{\"status\": 400, \"errors\": [\"Unacceptable or missing ACCEPT header!\"]}");
      log.warn("Cannot return data in formats: {}  - if you think this is wrong please check character encodings.",
               acceptsHeader);
      return;
    }

    String jsonMessage = req.getParameter(Parameter.jsonMessage.toString());
    Message message = (Message) decoder.decode(ByteBuffer.wrap(jsonMessage.getBytes(charset)),
                                               Message.class);
    message.encoding = contentType;
    if(message.subsystemName == null)
    {
      message.subsystemName = req.getRequestURL()
          .toString()
          .replaceFirst(".*/([^/?]+).*", "$1");
    }
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    if(data != null)
      IOUtils.copy(data, bytes);
    message.body = ByteBuffer.wrap(bytes.toByteArray());
    Response result = MessageConsumer.dispatchMessage(message, null, hostName);
    resp.setStatus(result.getStatus());
    resp.getOutputStream().write(encoder.encode(result).array());
  }
}
