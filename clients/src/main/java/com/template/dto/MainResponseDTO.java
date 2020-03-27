package com.template.dto;

import java.io.Serializable;

public class MainResponseDTO<T> implements Serializable {

    private T response;

    public MainResponseDTO(){
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
