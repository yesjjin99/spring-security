package com.cos.spring_security1.config.oauth.provider;

public interface OAuth2UserInfo {

    String getProviderId();
    String getProvider();
    String getName();
    String getEmail();

}
