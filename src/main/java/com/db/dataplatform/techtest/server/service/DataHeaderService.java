package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import java.util.Optional;

public interface DataHeaderService {
    void saveHeader(DataHeaderEntity entity);
    Optional<DataHeaderEntity> getDataByBlockName(String blockName);
}
