package com.fortumo.gateway.models;

public class ProviderRequest {
    String id;
    String messageId;
    String sender;
    String text;
    int receiver;
    String operator;
    String timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id: ").append(id).append(", ")
                .append("messageId: ").append(messageId).append(", ")
                .append("sender: ").append(sender).append(", ")
                .append("text: ").append(text).append("\n")
                .append("receiver: ").append(receiver).append(", ")
                .append("operator: ").append(operator).append(", ")
                .append("timestamp: ").append(timestamp);
        return stringBuilder.toString();
    }
}
