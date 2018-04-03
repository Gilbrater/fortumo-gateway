package com.fortumo.gateway.services;

import com.fortumo.gateway.models.MerchantRequest;
import com.fortumo.gateway.models.ProviderRequest;
import com.fortumo.gateway.utils.MerchantUrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProviderService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderService.class);

    @Autowired
    private JmsTemplate jmsTemplate;
    private final String providerToMerchantQueue="ProviderToMerchantQueue";

    @Value("${merchant.urls}")
    private String merchants;

    public String paymentNotification(ProviderRequest providerRequest){
        MerchantRequest merchantRequest = createMerchantRequest(providerRequest);
        String keyword = getKeyword(merchantRequest.getMessage());
        MerchantUrlUtil merchantURLSUtil = MerchantUrlUtil.getInstance(merchants);
        if(!merchantURLSUtil.hasMerchant(keyword)){
            logger.debug("Merchant not found for keyword: "+keyword+" "+merchantRequest.toString());
            return "Merchant not found";
        }
        merchantRequest.setKeyword(keyword);
        logger.info(merchantRequest.toString());
        return (sendToMerchant(merchantRequest))?"OK":"FAILED";
    }

    private boolean sendToMerchant(MerchantRequest merchantRequest){
        try{
            jmsTemplate.convertAndSend(providerToMerchantQueue, merchantRequest);
        }catch (JmsException e){
            logger.error(e.getMessage(), merchantRequest.toString());
            return false;
        }
        return true;
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
