package com.sbouhaddi.fileManagement.service;

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
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sbouhaddi.fileManagement.Utils.EncryptionUtils;

@Service
public class FileStoreImpl implements FileStore {

	private final Path store = Paths.get("files");
	private final Path keyStore = Paths.get("keys");

	@Override
	public void init() throws IOException, NoSuchAlgorithmException {
		if (!Files.exists(store)) {
			Files.createDirectories(store);
		}
		if (!Files.exists(keyStore)) {
			Files.createDirectories(keyStore);
		}
		EncryptionUtils.generateAndSaveKey(keyStore.toString() + "/keyFile");
		EncryptionUtils.generateAndSaveIv(keyStore.toString() + "/ivFile");

	}

	@Override
	public void save(MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Path inputTargetLocation = store.resolve(fileName);
		Path outputTargetLocation = store.resolve("encoded_" + fileName);
		Files.copy(file.getInputStream(), inputTargetLocation, StandardCopyOption.REPLACE_EXISTING);
		File inputFile = inputTargetLocation.toFile();
		File outputFile = outputTargetLocation.toFile();
		SecretKey key = EncryptionUtils.getKey(keyStore.toString() + "/keyFile");
		IvParameterSpec iv = EncryptionUtils.getIv(keyStore.toString() + "/ivFile");
		EncryptionUtils.processFile(Cipher.ENCRYPT_MODE, key, iv, inputFile, outputFile);
		

	}

	@Override
	public Resource download(String fileName)
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
		Path inputTargetLocation = store.resolve("encoded_" + fileName);
		Path outputTargetLocation = store.resolve(fileName);
		File inputFile = inputTargetLocation.toFile();
		File outputFile = outputTargetLocation.toFile();
		SecretKey key = EncryptionUtils.getKey(keyStore.toString() + "/keyFile");
		IvParameterSpec iv = EncryptionUtils.getIv(keyStore.toString() + "/ivFile");
		EncryptionUtils.processFile(Cipher.DECRYPT_MODE, key, iv, inputFile, outputFile);

		return new UrlResource(outputTargetLocation.toUri());
	}

	@Override
	public void deleteAll() throws IOException {
		FileSystemUtils.deleteRecursively(store);
		FileSystemUtils.deleteRecursively(keyStore);

	}

	@Override
	public Stream<Path> getFiles() throws IOException {
		return Files.walk(store, 1).filter(path -> !path.equals(store)).map(store::relativize);
	}

}
