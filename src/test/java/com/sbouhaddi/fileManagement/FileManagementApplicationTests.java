package com.sbouhaddi.fileManagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.sbouhaddi.fileManagement.controller.FileManagementController;
import com.sbouhaddi.fileManagement.service.FileStore;

@SpringBootTest
@AutoConfigureMockMvc
class FileManagementApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private FileManagementController controller;

	@MockBean
	private FileStore fileStore;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

	@Test
	public void should_list_all_files() throws Exception {
		given(this.fileStore.getFiles()).willReturn(Stream.of(Paths.get("first.txt"), Paths.get("second.txt")));

		this.mvc.perform(get("/api/v1/files")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.[0].name").value("first.txt"))
				.andExpect(jsonPath("$.[0].url").value("http://localhost/api/v1/download/first.txt"))
				.andExpect(jsonPath("$.[1].name").value("second.txt"))
				.andExpect(jsonPath("$.[1].url").value("http://localhost/api/v1/download/second.txt"));
	}

	@Test
	public void should_upload_file() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain",
				"File to encode".getBytes());
		this.mvc.perform(multipart("/api/v1/upload").file(multipartFile)).andExpect(status().isOk())
				.andExpect(content().string("File Uploaded !test.txt"));

		then(this.fileStore).should().save(multipartFile);
	}

	@Test
	public void should_download_file() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain",
				"File to encode".getBytes());
		this.mvc.perform(get("/api/v1/download/" + multipartFile.getName())).andExpect(status().isOk());

		then(this.fileStore).should().download(multipartFile.getName());
	}

}
