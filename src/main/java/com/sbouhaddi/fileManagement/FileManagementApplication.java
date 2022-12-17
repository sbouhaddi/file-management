package com.sbouhaddi.fileManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sbouhaddi.fileManagement.service.FileStore;

import jakarta.annotation.Resource;

@SpringBootApplication
public class FileManagementApplication {

	@Resource
	FileStore fileStore;

	public static void main(String[] args) {
		SpringApplication.run(FileManagementApplication.class, args);
	}
}
