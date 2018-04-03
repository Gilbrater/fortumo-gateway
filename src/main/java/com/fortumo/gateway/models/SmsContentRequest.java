package com.fortumo.gateway.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SmsContentRequest extends Request{
    private String message;
    @JsonProperty("mo_message_id")
    private String moMessageId;
    private String receiver;
    private String operator;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMoMessageId() {
        return moMessageId;
    }

    public void setMoMessageId(String moMessageId) {
        this.moMessageId = moMessageId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id: ").append(getId()).append(", ")
                .append("message: ").append(message).append(", ")
                .append("moMessageId: ").append(moMessageId).append(", ")
                .append("receiver: ").append(receiver).append(", ")
                .append("operator: ").append(operator).append(", ");
        return stringBuilder.toString();
    }
}
