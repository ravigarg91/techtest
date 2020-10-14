package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.exception.DataNotFoundException;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private final DataBodyService dataBodyServiceImpl;
    private final DataHeaderService dataHeaderServiceImpl;
    private final ModelMapper modelMapper;
    public static final String MD5_CHECKSUM = "cecfd3953783df706878aaec2c22aa70";

    @Autowired
    private RestTemplate restTemplate;

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope) {
        boolean checkCal=calculateCheckSum(envelope);
        if(checkCal) {
            // Save to persistence.
            persist(envelope);
            log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
            return true;
        }
        return false;
    }



    @Override
    public DataEnvelope findByBlockType(String blockType) {
        log.info("Getting data by BlockType {}" ,blockType);
        DataBodyEntity dataEntity=dataBodyServiceImpl.getDataByBlockType(Enum.valueOf(BlockTypeEnum.class, blockType));
        if(dataEntity!=null){
        DataBody dataBody = DataBody.builder().dataBody(dataEntity.getDataBody()).build();
        DataEnvelope dataEnvelope = DataEnvelope.builder().dataBody(dataBody).build();
        return dataEnvelope;}
        return null;
    }

    @Override
    public boolean updateBlockTypeBasedOnBlockName(String blockName, String newBlockType) {
        log.info("Updating data by BlockName {}" ,blockName);
        DataHeaderEntity dataHeaderEntity=dataHeaderServiceImpl.getDataByBlockName(blockName).orElseThrow(DataNotFoundException::new);
        dataHeaderEntity.setBlocktype(Enum.valueOf(BlockTypeEnum.class, newBlockType));
        dataHeaderServiceImpl.saveHeader(dataHeaderEntity);
        return true;
    }

    private void persist(DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);
        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        saveData(dataBodyEntity);
    }

    private boolean calculateCheckSum(DataEnvelope envelope){
        try {
             MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(envelope.getDataBody().getDataBody().getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            if(hashtext.equalsIgnoreCase(MD5_CHECKSUM)){
                return true;
            }
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }



}
