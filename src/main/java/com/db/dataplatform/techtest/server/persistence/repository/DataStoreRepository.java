package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataStoreRepository extends JpaRepository<DataBodyEntity, Long> {

    @Query(value = "SELECT d.* FROM DATA_STORE d join DATA_HEADER h on h.data_header_id=d.data_header_id where blocktype=:blockType", nativeQuery = true)
    DataBodyEntity findByBlockTypeA(@Param("blockType") String blockType);


}
