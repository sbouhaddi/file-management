package com.sbouhaddi.fileencryption.exhandler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class FileExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public void handleMaxSize(MaxUploadSizeExceededException ex, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.EXPECTATION_FAILED.value(), "File max size Exceeded !");
	}
}
