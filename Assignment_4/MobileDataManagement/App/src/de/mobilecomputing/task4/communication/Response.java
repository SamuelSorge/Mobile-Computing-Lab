package de.mobilecomputing.task4.communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SebastianHesse on 06.07.2015.
 */
public class Response<T> implements Serializable {

    private static final long serialVersionUID = -4062801965427787536L;

    private T responseObject;

    public Response(T responseObject) {
        if (responseObject != null) {
            this.responseObject = responseObject;
        } else {
            throw new IllegalArgumentException("Response Object may not be null.");
        }
    }

    public T getResponseObject() {
        return this.responseObject;
    }
}
