package com.sbouhaddi.fileencryption.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.sbouhaddi.fileencryption.model.FileDetails;
import com.sbouhaddi.fileencryption.service.FileStore;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(FileManagementController.BASE_URL)
@RequiredArgsConstructor
//@CrossOrigin("http://localhost:8081")
public class FileManagementController {

	public static final String BASE_URL = "/api/v1";

	private final FileStore fileStore;

	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			fileStore.save(file);
			return ResponseEntity.ok("File Uploaded !" + file.getOriginalFilename());
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error uploading file !" + e.getMessage());
		}

	}

	@GetMapping("/download/{filename:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable @NotBlank String filename) {
		try {
			Resource downloadedFile = fileStore.download(filename);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename = \"" + filename + "\"")
					.body(downloadedFile);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| IOException e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}

	}

	@GetMapping("/files")
	public ResponseEntity<List<FileDetails>> getListFiles() {
		try {
			List<FileDetails> fileDetails = fileStore.getFiles().map(path -> {
				String filename = path.getFileName().toString();
				String url = MvcUriComponentsBuilder
						.fromMethodName(FileManagementController.class, "downloadFile", path.getFileName().toString())
						.build().toString();
				return new FileDetails(filename, url);

			}).toList();
			return ResponseEntity.ok(fileDetails);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}

	}
}
