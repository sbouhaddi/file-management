package com.sbouhaddi.fileencryption;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.sbouhaddi.fileencryption.Utils.EncryptionUtils;

@SpringBootTest
class EncryptDecryptFileTest {

	@Test
	void should_encrypt_decrypt_file_succeed() throws NoSuchAlgorithmException, IOException, InvalidKeyException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		List<String> lines = Arrays.asList("The first line", "The second line");
		Path file = Paths.get("inputFile.txt");
		Files.write(file, lines, StandardCharsets.UTF_8);

		File inputFile = file.toFile();
		File encryptedFile = new File("file.encrypted");
		File decryptedFile = new File("file.decrypted");

		EncryptionUtils.processFile(Cipher.ENCRYPT_MODE, inputFile, encryptedFile);
		EncryptionUtils.processFile(Cipher.DECRYPT_MODE, encryptedFile, decryptedFile);

		assertThat(inputFile).hasSameTextualContentAs(decryptedFile);

		encryptedFile.delete();
		decryptedFile.delete();
		Files.deleteIfExists(file);

	}

}
