package com.fortumo.gateway;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayApplicationTests {


    @LocalServerPort
    private int port;

    private StringBuilder base;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp(){
        this.base = new StringBuilder("http://localhost:").append(port).append("/api/v1/sms");
    }

    @Test
    public void receivePaymentNotificationSuccess(){
        base.append("?message_id=e39ce00e-f8b5-4b0b-96ce-d68f94525704&operator=Etisalat&receiver=13011&sender=%2B37255555555&text=TXT+COINS&timestamp=2017-11-03+12%3A32%3A13");
        ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);
        assertThat(response.getBody(), equalTo("OK"));
    }

    @Test
    public void receivePaymentNotificationInvalidMerchantCode(){
        base.append("?message_id=e39ce00e-f8b5-4b0b-96ce-d68f94525704&operator=Etisalat&receiver=13011&sender=%2B37255555555&text=THT+COINS&timestamp=2017-11-03+12%3A32%3A13");
        ResponseEntity<String> response = restTemplate.getForEntity(base.toString(), String.class);
        assertThat(response.getBody(), equalTo("Merchant not found"));
    }
}
