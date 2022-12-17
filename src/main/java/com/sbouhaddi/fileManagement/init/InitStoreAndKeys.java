package com.sbouhaddi.fileManagement.init;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sbouhaddi.fileManagement.service.FileStore;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitStoreAndKeys implements CommandLineRunner {

	private final FileStore fileStore;

	@Override
	public void run(String... args) throws NoSuchAlgorithmException, IOException {
		fileStore.init();

	}

	@PreDestroy
	public void onExit() throws IOException {
		fileStore.deleteAll();
	}

}
