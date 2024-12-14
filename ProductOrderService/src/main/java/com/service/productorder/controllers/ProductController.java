package com.service.productorder.controllers;

import com.service.productorder.config.AppConstants;
import com.service.productorder.entites.Product;
import com.service.productorder.dtos.ProductDTO;
import com.service.productorder.dtos.ProductResponseDTO;
import com.service.productorder.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/public/products")
	public ResponseEntity<ProductResponseDTO> getAllProducts(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

		ProductResponseDTO productResponseDTO = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);

		return new ResponseEntity<ProductResponseDTO>(productResponseDTO, HttpStatus.FOUND);
	}

	@GetMapping("/public/categories/{categoryId}/products")
	public ResponseEntity<ProductResponseDTO> getProductsByCategory(@PathVariable Long categoryId,
																	@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
																	@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
																	@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
																	@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

		ProductResponseDTO productResponseDTO = productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy,
				sortOrder);

		return new ResponseEntity<ProductResponseDTO>(productResponseDTO, HttpStatus.FOUND);
	}
	
	@GetMapping("/public/products/keyword/{keyword}")
	public ResponseEntity<ProductResponseDTO> getProductsByKeyword(@PathVariable String keyword,
																   @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
																   @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
																   @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
																   @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

		ProductResponseDTO productResponseDTO = productService.searchProductByKeyword(keyword, pageNumber, pageSize, sortBy,
				sortOrder);

		return new ResponseEntity<ProductResponseDTO>(productResponseDTO, HttpStatus.FOUND);
	}

	@PostMapping("/admin/categories/{categoryId}/product")
	public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody Product product, @PathVariable Long categoryId) {

		ProductDTO savedProduct = productService.addProduct(categoryId, product);
		return new ResponseEntity<ProductDTO>(savedProduct, HttpStatus.CREATED);
	}

	@PutMapping("/admin/products/{productId}")
	public ResponseEntity<ProductDTO> updateProduct(@RequestBody Product product,
			@PathVariable Long productId) {
		ProductDTO updatedProduct = productService.updateProduct(productId, product);

		return new ResponseEntity<ProductDTO>(updatedProduct, HttpStatus.OK);
	}
	
	@PutMapping("/admin/products/{productId}/image")
	public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image") MultipartFile image) throws IOException {
		ProductDTO updatedProduct = productService.updateProductImage(productId, image);

		return new ResponseEntity<ProductDTO>(updatedProduct, HttpStatus.OK);
	}

	@DeleteMapping("/admin/products/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
		String status = productService.deleteProduct(productId);

		return new ResponseEntity<String>(status, HttpStatus.OK);
	}

}
