package com.fortumo.gateway.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MerchantRequest extends Request{
    private int shortcode;
    private String keyword;
    private String message;
    private String operator;
    private String sender;
    @JsonProperty("transaction_id")
    private String transactionId;
    private String moMessageId;

    public int getShortcode() {
        return shortcode;
    }

    public void setShortcode(int shortcode) {
        this.shortcode = shortcode;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMoMessageId() {
        return moMessageId;
    }

    public void setMoMessageId(String moMessageId) {
        this.moMessageId = moMessageId;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id: ").append(getId()).append(", ")
                .append("message: ").append(message).append(", ")
                .append("moMessageId: ").append(moMessageId).append(", ")
                .append("sender: ").append(sender).append(", ")
                .append("shortcode: ").append(shortcode).append(", ")
                .append("operator: ").append(operator).append(", ")
                .append("keyword: ").append(keyword).append(", ")
                .append("transactionId: ").append(transactionId);
        return stringBuilder.toString();
    }
}
