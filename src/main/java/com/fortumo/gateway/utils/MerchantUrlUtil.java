package com.fortumo.gateway.utils;

import java.util.HashMap;
import java.util.Map;

public class MerchantUrlUtil {
    private static String merchants;
    private static Map<String, String> merchantTable;

    private static MerchantUrlUtil instance;

    private MerchantUrlUtil(){}

    public static MerchantUrlUtil getInstance(String allMerchants){
        if (instance==null) {
            instance = new MerchantUrlUtil();
            merchants=allMerchants;
            populateMerchantTable();
        }
        return instance;
    }

    private static void populateMerchantTable(){
        merchantTable = new HashMap<>();
        String[] links = merchants.split(",");
        for(String merchantLink:links){
            String[] merchant = merchantLink.split("@");
            merchantTable.put(merchant[0], merchant[1]);
        }
    }


    public String getMerchantURL(String merchantName){
        return merchantTable.get(merchantName);
    }

    public boolean hasMerchant(String merchantName){
        return merchantTable.containsKey(merchantName);
    }
}
