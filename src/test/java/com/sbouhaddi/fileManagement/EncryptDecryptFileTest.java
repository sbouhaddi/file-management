package com.sbouhaddi.fileManagement;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.sbouhaddi.fileManagement.Utils.EncryptionUtils;

@SpringBootTest
public class EncryptDecryptFileTest {

	@Test
	void should_encrypt_decrypt_file_succeed() throws NoSuchAlgorithmException, IOException, InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		Path path = Paths.get("tests");

		SecretKey key = EncryptionUtils.getKey();
		IvParameterSpec iv = EncryptionUtils.getIv();

		Path file = path.resolve("inputFile.txt");
		File inputFile = file.toFile();
		File encryptedFile = new File("file.encrypted");
		File decryptedFile = new File("file.decrypted");

		EncryptionUtils.processFile(Cipher.ENCRYPT_MODE, key, iv, inputFile, encryptedFile);
		EncryptionUtils.processFile(Cipher.DECRYPT_MODE, key, iv, encryptedFile, decryptedFile);

		assertThat(inputFile).hasSameTextualContentAs(decryptedFile);

		encryptedFile.delete();
		decryptedFile.delete();

	}

}
