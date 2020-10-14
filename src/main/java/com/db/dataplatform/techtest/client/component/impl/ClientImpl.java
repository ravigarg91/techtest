package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.RestTemplateConfiguration;
import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    public static final String URI_PUSHDATA = "http://localhost:8092/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8092/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8092/dataserver/update/{name}/{newBlockType}");

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void pushData(DataEnvelope dataEnvelope) {
        Boolean valueFromServer= restTemplate.postForObject(URI_PUSHDATA, dataEnvelope,Boolean.class);
        log.info("Got from server {}",valueFromServer);
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
    }

    @Override
    public DataEnvelope getData(String blockType) {
        log.info("Query for data with header block type {}", blockType);
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("blockType", blockType);
        DataEnvelope dataEnvelope = restTemplate.getForObject(URI_GETDATA.toString(), DataEnvelope.class, urlParams);
        return dataEnvelope;
    }

    @Override
    public Boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        Map<String, String> urlParams = new HashMap<String, String>();
        urlParams.put("name", blockName);
        urlParams.put("newBlockType", newBlockType);
        restTemplate.patchForObject(URI_PATCHDATA.toString(), String.class,Boolean.class,urlParams);
        return true;
    }


}
