package com.bn.mapper;

import com.bn.persistence.AppDO;
import org.springframework.stereotype.Repository;

@Repository
public interface AppMapper {
    AppDO selectById(String id);
}
