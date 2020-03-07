package ru.ashamaz.model;

import java.io.Serializable;

/**
 * An Entity for Command of type REQUEST
 */
public class RqCommand implements Serializable {
    private String requestBody;
    private String responseBody;

    public RqCommand() {
    }

    public RqCommand(String requestBody, String responseBody) {
        this.requestBody = requestBody;
        this.responseBody = responseBody;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
