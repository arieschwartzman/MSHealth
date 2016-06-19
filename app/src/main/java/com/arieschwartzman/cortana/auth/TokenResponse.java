package com.arieschwartzman.cortana.auth;

/**
 * Created by ariesch on 17-Jun-16.
 */
public class TokenResponse {
    private String access_token;
    private String refresh_token;
    private long expires_in;

    public String getAccess_token() { return access_token;}

    public String getRefresh_token() { return  refresh_token;}

    public long getExpires_in() { return  expires_in;}
}
