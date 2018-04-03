package com.fortumo.gateway.consumers;

import com.fortumo.gateway.clients.MerchantClient;
import com.fortumo.gateway.models.MerchantRequest;
import com.fortumo.gateway.models.MerchantResponse;
import com.fortumo.gateway.models.Request;
import com.fortumo.gateway.models.SmsContentRequest;
import com.fortumo.gateway.utils.RetryQueueInsertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

@Component
public class MerchantConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MerchantConsumer.class);

    @Autowired
    private MerchantClient merchantClient;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${merchant.urls}")
    private String merchants;

    private final String merchantToProviderQueue = "MerchantToProviderQueue";
    private final String providerToMerchantQueue="ProviderToMerchantQueue";

    private ConcurrentMap<String, ExecutorService> providerToMerchantQueueRetryTable;
    private ConcurrentMap<String, ExecutorService> merchantToProviderQueueRetryTable;

    public MerchantConsumer(){
        merchantToProviderQueueRetryTable = new ConcurrentHashMap<>();
        providerToMerchantQueueRetryTable = new ConcurrentHashMap<>();
    }

    @JmsListener(destination = providerToMerchantQueue, containerFactory = "myFactory")
    public void receiveFromProviderToMerchantQueue(MerchantRequest merchantRequest){
        logger.info("Message picked by merchant - ",merchantRequest.toString());
        MerchantResponse merchantResponse  = merchantClient.notifyMerchant(merchantRequest);
        if(!merchantResponse.hasError()){
            SmsContentRequest smsContentRequest = createSMSContentRequest(merchantRequest, merchantResponse);
            sendToMerchantToProviderQueue(smsContentRequest);
            logger.info("Message queued by merchant - " + merchantRequest.toString());
        }else{
            sendToProviderToMerchantQueue(merchantRequest);
            logger.info("Message requeued by retry - "+ merchantRequest.toString());
        }
    }

    private void sendToProviderToMerchantQueue(MerchantRequest merchantRequest){
        try{
            jmsTemplate.convertAndSend(providerToMerchantQueue, merchantRequest);
        }catch (JmsException | NullPointerException e){
            logger.error("Message not queued in providerToMerchantQueue - "+ merchantRequest.toString()+ " - " +e.getMessage());
            providerToMerchantQueueRetryTable=RetryQueueInsertUtil.RetryQueueInsert(merchantRequest, providerToMerchantQueueRetryTable);
        }
    }

    private void sendToMerchantToProviderQueue(SmsContentRequest smsContentRequest){
        try{
            jmsTemplate.convertAndSend(merchantToProviderQueue, smsContentRequest);
        }catch (JmsException | NullPointerException e){
            logger.error("Message not queued in merchantToProviderQueue - "+ smsContentRequest.toString()+ " - " +e.getMessage());
            merchantToProviderQueueRetryTable=RetryQueueInsertUtil.RetryQueueInsert(smsContentRequest, merchantToProviderQueueRetryTable);
        }
    }

    private SmsContentRequest createSMSContentRequest(MerchantRequest merchantRequest, MerchantResponse merchantNotificationResponse){
        SmsContentRequest smsContentRequest = new SmsContentRequest();
        smsContentRequest.setReceiver(merchantRequest.getSender());
        smsContentRequest.setOperator(merchantRequest.getOperator());
        smsContentRequest.setMoMessageId(merchantRequest.getMoMessageId());
        smsContentRequest.setMessage(merchantNotificationResponse.getReplyMessage());
        smsContentRequest.setId(merchantRequest.getTransactionId());
        return smsContentRequest;
    }
}
