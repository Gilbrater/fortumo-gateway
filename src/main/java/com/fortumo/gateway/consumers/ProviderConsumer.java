package com.fortumo.gateway.consumers;

import com.fortumo.gateway.clients.ProviderClient;
import com.fortumo.gateway.models.SmsContentRequest;
import com.fortumo.gateway.utils.RetryQueueInsertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

@Component
public class ProviderConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ProviderConsumer.class);
    @Autowired
    private ProviderClient providerClient;

    @Autowired
    private JmsTemplate jmsTemplate;

    private final String merchantToProviderQueue = "MerchantToProviderQueue";
    private ConcurrentMap<String, ExecutorService> merchantToProviderQueueRetryTable;

    public ProviderConsumer(){
        merchantToProviderQueueRetryTable = new ConcurrentHashMap<>();
    }

    @JmsListener(destination = merchantToProviderQueue, containerFactory = "myFactory")
    public void receiveFromMerchantToProviderQueue(SmsContentRequest smsContentRequest){
        if(!providerClient.notifyUser(smsContentRequest)){
            sendToMerchantToProviderQueue(smsContentRequest);
        }
    }

    private void sendToMerchantToProviderQueue(SmsContentRequest smsContentRequest){
        try{
            jmsTemplate.convertAndSend(merchantToProviderQueue, smsContentRequest);
        }catch (JmsException e){
            logger.error(e.getMessage());
            merchantToProviderQueueRetryTable=RetryQueueInsertUtil.RetryQueueInsert(smsContentRequest, merchantToProviderQueueRetryTable);
        }

    }
}
