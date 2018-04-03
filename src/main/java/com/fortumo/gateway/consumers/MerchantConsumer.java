package com.fortumo.gateway.consumers;

import com.fortumo.gateway.clients.MerchantClient;
import com.fortumo.gateway.models.MerchantRequest;
import com.fortumo.gateway.models.MerchantResponse;
import com.fortumo.gateway.models.Request;
import com.fortumo.gateway.models.SmsContentRequest;
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
            logger.info("Message queued by merchant - ",merchantRequest.toString());
        }else{
            sendToProviderToMerchantQueue(merchantRequest);
        }
    }

    private void sendToProviderToMerchantQueue(MerchantRequest merchantRequest){
        try{
            jmsTemplate.convertAndSend(providerToMerchantQueue, merchantRequest);
        }catch (JmsException e){
            logger.error(e.getMessage());
            placeMessageInQueueRetryTable(merchantRequest, providerToMerchantQueueRetryTable);
        }
    }

    private void sendToMerchantToProviderQueue(SmsContentRequest smsContentRequest){
        try{
            jmsTemplate.convertAndSend(merchantToProviderQueue, smsContentRequest);
        }catch (JmsException e){
            logger.error(e.getMessage());
            placeMessageInQueueRetryTable(smsContentRequest, providerToMerchantQueueRetryTable);
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

    private void placeMessageInQueueRetryTable(Request Request, ConcurrentMap queueRetryTable){
        //Create ExecutorService for each failed attempt to put in queue
    }
}
