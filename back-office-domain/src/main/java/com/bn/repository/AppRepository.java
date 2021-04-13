package com.bn.repository;

import com.bn.domain.App;

public interface AppRepository {
    App get(String id);
}
