package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;

import java.util.List;
import java.util.Optional;

public interface DataBodyService {
    void saveDataBody(DataBodyEntity dataBody);
    DataBodyEntity getDataByBlockType(BlockTypeEnum blockType);
   
}
