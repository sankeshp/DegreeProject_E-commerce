package com.service.productorder.exceptions;

import com.service.productorder.controllers.CartController;
import com.service.productorder.dtos.APIResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

	private static final Logger logger = LogManager.getLogger(CartController.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<APIResponseDTO> myResourceNotFoundException(ResourceNotFoundException e) {
		String message = e.getMessage();

		APIResponseDTO res = new APIResponseDTO(message, false);
		logger.error("An error occurred: ", e);
		return new ResponseEntity<APIResponseDTO>(res, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(APIException.class)
	public ResponseEntity<APIResponseDTO> myAPIException(APIException e) {
		String message = e.getMessage();

		APIResponseDTO res = new APIResponseDTO(message, false);
		logger.error("An error occurred: ", e);
		return new ResponseEntity<APIResponseDTO>(res, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		Map<String, String> res = new HashMap<>();

		e.getBindingResult().getAllErrors().forEach(err -> {
			String fieldName = ((FieldError) err).getField();
			String message = err.getDefaultMessage();

			res.put(fieldName, message);
		});
		logger.error("An error occurred: ", e);
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> myConstraintsVoilationException(ConstraintViolationException e) {
		Map<String, String> res = new HashMap<>();

		e.getConstraintViolations().forEach(voilation -> {
			String fieldName = voilation.getPropertyPath().toString();
			String message = voilation.getMessage();

			res.put(fieldName, message);
		});
		logger.error("An error occurred: ", e);
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingPathVariableException.class)
	public ResponseEntity<APIResponseDTO> myMissingPathVariableException(MissingPathVariableException e) {
		APIResponseDTO res = new APIResponseDTO(e.getMessage(), false);
		logger.error("An error occurred: ", e);
		return new ResponseEntity<APIResponseDTO>(res, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<APIResponseDTO> myDataIntegrityException(DataIntegrityViolationException e) {
		APIResponseDTO res = new APIResponseDTO(e.getMessage(), false);
		logger.error("An error occurred: ", e);
		return new ResponseEntity<APIResponseDTO>(res, HttpStatus.BAD_REQUEST);
	}
}
