package com.sbouhaddi.fileManagement.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class EncryptionUtils {

	public static void generateAndSaveKey(String keyFile)
			throws NoSuchAlgorithmException, FileNotFoundException, IOException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		SecretKey key = keyGen.generateKey();
		try (FileOutputStream out = new FileOutputStream(keyFile)) {
			byte[] keyb = key.getEncoded();
			out.write(keyb);
		}
		;
	}

	public static SecretKey getKey(String keyFile) throws IOException {
		byte[] keyb = Files.readAllBytes(Paths.get(keyFile));
		SecretKey skey = new SecretKeySpec(keyb, "AES");
		return skey;
	}

	public static void generateAndSaveIv(String ivFile) throws FileNotFoundException, IOException {
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		try (FileOutputStream out = new FileOutputStream(ivFile)) {
			out.write(iv);
		}
		;
	}

	public static IvParameterSpec getIv(String ivFile) throws FileNotFoundException, IOException {

		byte[] iv = Files.readAllBytes(Paths.get(ivFile));
		return new IvParameterSpec(iv);
	}

	public static void processFile(int encryptMode, SecretKey key, IvParameterSpec iv, File inputFile, File outputFile)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(encryptMode, key, iv);
		FileInputStream inputStream = new FileInputStream(inputFile);
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		byte[] buffer = new byte[64];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			byte[] output = cipher.update(buffer, 0, bytesRead);
			if (output != null) {
				outputStream.write(output);

			}
		}

		byte[] outputBytes = cipher.doFinal();
		if (outputBytes != null) {
			outputStream.write(outputBytes);
		}

		inputStream.close();
		outputStream.close();
	}

}
