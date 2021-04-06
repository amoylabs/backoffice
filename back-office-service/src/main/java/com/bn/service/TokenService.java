package com.bn.service;

public interface TokenService {
    String generateIdempotentApiToken();

    boolean isIdempotentApiTokenConsumed(String token);
}
