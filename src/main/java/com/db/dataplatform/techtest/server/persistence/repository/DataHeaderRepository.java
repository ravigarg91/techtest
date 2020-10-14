package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DataHeaderRepository extends JpaRepository<DataHeaderEntity, Long> {
    
    @Query(value = "SELECT h.* FROM DATA_HEADER h where h.NAME=:blockName", nativeQuery = true)
    DataHeaderEntity findByBlockName(@Param("blockName") String blockName);

}
