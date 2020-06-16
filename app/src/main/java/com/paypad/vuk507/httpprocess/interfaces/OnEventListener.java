package com.paypad.vuk507.httpprocess.interfaces;

public interface OnEventListener<T> {

    public void onSuccess(T object);
    public void onFailure(Exception e);
    public void onTaskContinue();

}


