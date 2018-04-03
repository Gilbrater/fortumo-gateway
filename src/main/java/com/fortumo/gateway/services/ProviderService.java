package com.fortumo.gateway.services;

import com.fortumo.gateway.models.MerchantRequest;
import com.fortumo.gateway.models.ProviderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProviderService {
    public static final Logger logger = LoggerFactory.getLogger(ProviderService.class);

    public String paymentNotification(ProviderRequest providerRequest){
        MerchantRequest merchantRequest = createMerchantRequest(providerRequest);
        String keyword = getKeyword(merchantRequest.getMessage());
        merchantRequest.setKeyword(keyword);
        return "OK";
    }

    private MerchantRequest createMerchantRequest(ProviderRequest providerRequest){
        MerchantRequest merchantNotificationRequest = new MerchantRequest();
        merchantNotificationRequest.setMessage(providerRequest.getText());
        merchantNotificationRequest.setOperator(providerRequest.getOperator());
        merchantNotificationRequest.setSender(providerRequest.getSender());
        merchantNotificationRequest.setShortcode(providerRequest.getReceiver());
        merchantNotificationRequest.setTransactionId(providerRequest.getId());
        merchantNotificationRequest.setMoMessageId(providerRequest.getMessageId());
        return merchantNotificationRequest;
    }

    private String getKeyword(String message){
        return message.split(" ")[0];
    }


}
