package com.sbouhaddi.fileencryption.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EncryptionUtils {

	private static SecretKey key;
	private static IvParameterSpec iv;

	private EncryptionUtils() {
	}

	public static void init() throws NoSuchAlgorithmException {

		log.info("GENERATING KEY AND VECTOR ");
		KeyGenerator keyGen = KeyGenerator.getInstance(EncryptionConstants.AES.getValue());
		key = keyGen.generateKey();

		byte[] ivByte = new byte[16];
		new SecureRandom().nextBytes(ivByte);
		iv = new IvParameterSpec(ivByte);
	}

	public static SecretKey getKey() {
		return key;
	}

	public static IvParameterSpec getIv() {
		return iv;
	}

	public static void processFile(int encryptMode, File inputFile, File outputFile)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {

		log.info("PROCESSING FILE " + inputFile.getName());

		Cipher cipher = Cipher.getInstance(EncryptionConstants.AES_CIPHER.getValue());
		cipher.init(encryptMode, key, iv);
		try (FileInputStream inputStream = new FileInputStream(inputFile);
				FileOutputStream outputStream = new FileOutputStream(outputFile)) {

			byte[] buffer = new byte[1024];
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

		}

		log.info("FILE PROCESSED SUCCESFULLY IN " + outputFile.getName());
	}

}
