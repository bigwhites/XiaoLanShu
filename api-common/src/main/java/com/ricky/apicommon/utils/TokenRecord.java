package com.ricky.apicommon.utils;

import java.util.Date;

public class TokenRecord {
   public String token;
    public Date expireDateTime;

    public TokenRecord(String token, Date expireDateTime) {
        this.token = token;
        this.expireDateTime = expireDateTime;
    }
}

