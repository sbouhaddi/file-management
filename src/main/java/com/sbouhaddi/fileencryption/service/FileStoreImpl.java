package com.sbouhaddi.fileencryption.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sbouhaddi.fileencryption.utilities.EncryptionUtils;

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

		log.info("UPLOAD STARTED  ");
		Path inputTargetLocation = Files.createTempFile(store, "input", ".tmp");
		Path outputTargetLocation = store.resolve(file.getOriginalFilename());
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

		log.info("DOWNLOAD STARTED  ");
		Path inputTargetLocation = store.resolve(fileName);
		Path tmpFile;

		if (SystemUtils.IS_OS_UNIX) {
			FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions
					.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
			tmpFile = Files.createTempFile("output", ".tmp", attr);
		} else {
			File f = Files.createTempFile("output", ".tmp").toFile();
			f.setReadable(true, true);
			f.setWritable(true, true);
			f.setExecutable(true, true);
			tmpFile = f.toPath();
		}

		Path outputTargetLocation = store.resolve(tmpFile);
		File inputFile = inputTargetLocation.toFile();
		File outputFile = outputTargetLocation.toFile();

		EncryptionUtils.processFile(Cipher.DECRYPT_MODE, inputFile, outputFile);
		log.info("FILE DECRYPTED IN  " + outputTargetLocation);
		outputFile.deleteOnExit();
		return new UrlResource(outputTargetLocation.toUri());
	}

	@Override
	public void deleteAll() throws IOException {
		log.info("CLEANING STORE ");
		FileSystemUtils.deleteRecursively(store);

	}

	@Override
	public Stream<Path> getFiles() throws IOException {
		log.info("LIST ALL FILES IN STORE ");
		return Files.walk(store, 1).filter(path -> !path.equals(store)).map(store::relativize);
	}

}
