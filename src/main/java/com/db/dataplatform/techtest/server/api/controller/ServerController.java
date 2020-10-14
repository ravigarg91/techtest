package com.db.dataplatform.techtest.server.api.controller;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.exception.DataNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated

public class ServerController {

    private final Server server;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> pushData(@Valid @RequestBody DataEnvelope dataEnvelope) throws IOException, NoSuchAlgorithmException {
        log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
        boolean checksumPass = server.saveDataEnvelope(dataEnvelope);
        if(checksumPass) {pushDataToHadoop(dataEnvelope.toString());}
        log.info("checksum: {}", checksumPass);
        log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
        return ResponseEntity.ok(checksumPass);
    }
    

    @GetMapping(value = "/data/{blockType}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DataEnvelope> getData(@PathVariable("blockType") String blockType) {
        HttpStatus status = HttpStatus.OK;
        DataEnvelope byBlockType = server.findByBlockType(blockType);
        log.info("Block Type: {}", blockType);
        return ResponseEntity.status(status).body(byBlockType);
    }

    @PatchMapping(path = "/update/{name}/{newBlockType}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity updateCustomer(@PathVariable("name") String blockName, @PathVariable("newBlockType") String newBlockType) {
        Boolean patchResult = server.updateBlockTypeBasedOnBlockName(blockName,newBlockType);
        return ResponseEntity.ok().build();

    }

    @Async
    public CompletableFuture pushDataToHadoop(String payload) {
        log.info("Pushing Data to Hadoop");
        String url = String.format("http://localhost:8092/hadoopserver/pushbigdata");
        String result = restTemplate.postForObject(url, payload, String.class);
        return CompletableFuture.completedFuture(result);
    }



}
