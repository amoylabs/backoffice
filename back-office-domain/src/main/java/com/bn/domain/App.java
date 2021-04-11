package com.bn.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class App {
    String id;
    String name;
    String signingKey;
}
