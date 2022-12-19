package com.sbouhaddi.fileencryption.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sbouhaddi.fileencryption.Utils.EncryptionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileStoreImpl implements FileStore {

	@Value("${files.local.store}")
	private String localStorePath;
	private Path store;

	@Override
	public void init() throws IOException, NoSuchAlgorithmException {

		store = Paths.get(localStorePath);
		log.info("FILES STORE " + localStorePath);
		if (!Files.exists(store)) {
			Files.createDirectories(store);
		}
		EncryptionUtils.init();

	}

	@Override
	public void save(MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Path inputTargetLocation = Files.createTempFile(fileName, "");
		Path outputTargetLocation = store.resolve(fileName);
		Files.copy(file.getInputStream(), inputTargetLocation, StandardCopyOption.REPLACE_EXISTING);
		File inputFile = inputTargetLocation.toFile();
		File outputFile = outputTargetLocation.toFile();

		EncryptionUtils.processFile(Cipher.ENCRYPT_MODE, inputFile, outputFile);
		log.info("FILE ENCRYPTED IN  " + outputFile.toPath());
		Files.delete(inputTargetLocation);

	}

	@Override
	public Resource download(String fileName)
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
		Path inputTargetLocation = store.resolve(fileName);
		Path outputTargetLocation = store.resolve(Files.createTempFile(fileName, ""));
		File inputFile = inputTargetLocation.toFile();
		File outputFile = outputTargetLocation.toFile();
		
		EncryptionUtils.processFile(Cipher.DECRYPT_MODE, inputFile, outputFile);
		log.info("FILE DECRYPTED IN  " + outputTargetLocation);
		outputFile.deleteOnExit();
		return new UrlResource(outputTargetLocation.toUri());
	}

	@Override
	public void deleteAll() throws IOException {
		FileSystemUtils.deleteRecursively(store);

	}

	@Override
	public Stream<Path> getFiles() throws IOException {
		return Files.walk(store, 1).filter(path -> !path.equals(store)).map(store::relativize);
	}

}
