package com.service.productorder.services;

import com.service.productorder.entites.Cart;
import com.service.productorder.entites.Category;
import com.service.productorder.entites.Product;
import com.service.productorder.exceptions.APIException;
import com.service.productorder.exceptions.ResourceNotFoundException;
import com.service.productorder.dtos.CartDTO;
import com.service.productorder.dtos.ProductDTO;
import com.service.productorder.dtos.ProductResponseDTO;
import com.service.productorder.repositories.CartRepo;
import com.service.productorder.repositories.CategoryRepo;
import com.service.productorder.repositories.ProductRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private CartRepo cartRepo;

	@Autowired
	private CartService cartService;

	@Autowired
	private FileService fileService;

	@Autowired
	private ModelMapper modelMapper;

	@Value("${project.image}")
	private String path;

	@Override
	public ProductDTO addProduct(Long categoryId, Product product) {

		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

		boolean isProductNotPresent = true;

		List<Product> products = category.getProducts();

        for (Product value : products) {
            if (value.getProductName().equals(product.getProductName())
                    && value.getDescription().equals(product.getDescription())) {

                isProductNotPresent = false;
                break;
            }
        }

		if (isProductNotPresent) {
			product.setImage("default.png");

			product.setCategory(category);

			double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
			product.setSpecialPrice(specialPrice);

			Product savedProduct = productRepo.save(product);

			return modelMapper.map(savedProduct, ProductDTO.class);
		} else {
			throw new APIException("Product already exists !!!");
		}
	}

	@Override
	public ProductResponseDTO getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Product> pageProducts = productRepo.findAll(pageDetails);

		List<Product> products = pageProducts.getContent();

		return getProductResponseDTO(pageProducts, products);
	}

	@Override
	public ProductResponseDTO searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
											   String sortOrder) {

		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Product> pageProducts = productRepo.findAll(pageDetails);

		List<Product> products = pageProducts.getContent();

		if (products.isEmpty()) {
			throw new APIException(category.getCategoryName() + " category doesn't contain any products !!!");
		}

		return getProductResponseDTO(pageProducts, products);
	}

	@Override
	public ProductResponseDTO searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Product> pageProducts = productRepo.findByProductNameLike(keyword, pageDetails);

		List<Product> products = pageProducts.getContent();
		
		if (products.isEmpty()) {
			throw new APIException("Products not found with keyword: " + keyword);
		}

		return getProductResponseDTO(pageProducts, products);
	}

	@Override
	public ProductDTO updateProduct(Long productId, Product product) {
		Product productFromDB = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		if (productFromDB == null) {
			throw new APIException("Product not found with productId: " + productId);
		}

		product.setImage(productFromDB.getImage());
		product.setProductId(productId);
		product.setCategory(productFromDB.getCategory());

		double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
		product.setSpecialPrice(specialPrice);

		Product savedProduct = productRepo.save(product);

		List<Cart> carts = cartRepo.findCartsByProductId(productId);

		List<CartDTO> cartDTOs = carts.stream().map(cart -> {
			CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

			List<ProductDTO> products = cart.getCartItems().stream()
					.map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

			cartDTO.setProducts(products);

			return cartDTO;

		}).toList();

		cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

		return modelMapper.map(savedProduct, ProductDTO.class);
	}

	@Override
	public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
		Product productFromDB = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		if (productFromDB == null) {
			throw new APIException("Product not found with productId: " + productId);
		}
		
		String fileName = fileService.uploadImage(path, image);
		
		productFromDB.setImage(fileName);
		
		Product updatedProduct = productRepo.save(productFromDB);
		
		return modelMapper.map(updatedProduct, ProductDTO.class);
	}
	
	@Override
	public String deleteProduct(Long productId) {

		Product product = productRepo.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		List<Cart> carts = cartRepo.findCartsByProductId(productId);

		carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

		productRepo.delete(product);

		return "Product with productId: " + productId + " deleted successfully !!!";
	}

	private ProductResponseDTO getProductResponseDTO(Page<Product> pageProducts, List<Product> products) {
		List<ProductDTO> productDTOs = products.stream().map(p -> modelMapper.map(p, ProductDTO.class))
				.collect(Collectors.toList());

		ProductResponseDTO productResponseDTO = new ProductResponseDTO();

		productResponseDTO.setContent(productDTOs);
		productResponseDTO.setPageNumber(pageProducts.getNumber());
		productResponseDTO.setPageSize(pageProducts.getSize());
		productResponseDTO.setTotalElements(pageProducts.getTotalElements());
		productResponseDTO.setTotalPages(pageProducts.getTotalPages());
		productResponseDTO.setLastPage(pageProducts.isLast());

		return productResponseDTO;
	}
}
