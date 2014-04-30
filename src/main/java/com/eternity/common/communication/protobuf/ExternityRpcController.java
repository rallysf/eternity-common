package com.eternity.common.communication.protobuf;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class ExternityRpcController
implements RpcController
{
  private String error = null;
  private boolean failed = false;

  @Override
  public String errorText()
  {
    return error;
  }

  @Override
  public boolean failed()
  {
    return failed;
  }

  @Override
  public boolean isCanceled()
  {
    return false;
  }

  @Override
  public void notifyOnCancel(RpcCallback<Object> cb)
  {
    throw new UnsupportedOperationException("Cannot cancel!");
  }

  @Override
  public void reset()
  {
    failed = false;
    error = null;
  }

  @Override
  public void setFailed(String error)
  {
    failed = true;
    this.error = error; 
  }

  @Override
  public void startCancel()
  {
    throw new UnsupportedOperationException("Cannot cancel!");
  }

}
