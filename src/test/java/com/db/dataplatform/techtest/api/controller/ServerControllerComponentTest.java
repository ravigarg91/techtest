package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.HadoopDummyServerController;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerComponentTest {

	public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
	public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/");
	public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/");
	public static final String URI_PUSH_HADOOP_DATA = "http://localhost:8090/hadoopserver/pushbigdata";

	@Mock
	private Server serverMock;

	private DataEnvelope testDataEnvelope;
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;
	private MockMvc mockHadoopMvc;
	private ServerController serverController;
	private HadoopDummyServerController hadoopDummyServerController;

	@Before
	public void setUp() throws HadoopClientException, NoSuchAlgorithmException, IOException {
		serverController = new ServerController(serverMock);
		hadoopDummyServerController = new HadoopDummyServerController(serverMock);
		mockMvc = standaloneSetup(serverController).build();
		mockHadoopMvc= standaloneSetup(hadoopDummyServerController).build();
		objectMapper = Jackson2ObjectMapperBuilder
				.json()
				.build();

		testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();

	}

	@Test
	public void testQueryDataGetCallWorksAsExpected() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get(URI_GETDATA + BlockTypeEnum.BLOCKTYPEA.name()))
				.andExpect(status().isOk()).andReturn();
		assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
	}

	@Test
	public void testUpdateDataGetCallWorksAsExpected() throws Exception {
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA + TEST_NAME+ "/"+ BlockTypeEnum.BLOCKTYPEA.name()))
				.andExpect(status().isOk()).andReturn();
		assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
	}
}
