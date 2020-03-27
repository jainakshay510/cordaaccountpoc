package com.template.dto;

import java.io.Serializable;

public class MainRequestDTO<T> implements Serializable {

    private T request;

    public MainRequestDTO(){};

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }
}
