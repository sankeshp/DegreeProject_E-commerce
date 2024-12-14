package com.service.productorder.services;

import com.service.productorder.entites.Category;
import com.service.productorder.entites.Product;
import com.service.productorder.exceptions.APIException;
import com.service.productorder.exceptions.ResourceNotFoundException;
import com.service.productorder.dtos.CategoryDTO;
import com.service.productorder.dtos.CategoryResponseDTO;
import com.service.productorder.repositories.CategoryRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepo categoryRepo;
	
	@Autowired
	private ProductService productService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public CategoryDTO createCategory(Category category) {
		Category savedCategory = categoryRepo.findByCategoryName(category.getCategoryName());

		if (savedCategory != null) {
			throw new APIException("Category with the name '" + category.getCategoryName() + "' already exists !!!");
		}

		savedCategory = categoryRepo.save(category);

		return modelMapper.map(savedCategory, CategoryDTO.class);
	}

	@Override
	public CategoryResponseDTO getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		
		Page<Category> pageCategories = categoryRepo.findAll(pageDetails);

		List<Category> categories = pageCategories.getContent();

		if (categories.isEmpty()) {
			throw new APIException("No category is created till now");
		}

		List<CategoryDTO> categoryDTOs = categories.stream()
				.map(category -> modelMapper.map(category, CategoryDTO.class)).collect(Collectors.toList());

		CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
		
		categoryResponseDTO.setContent(categoryDTOs);
		categoryResponseDTO.setPageNumber(pageCategories.getNumber());
		categoryResponseDTO.setPageSize(pageCategories.getSize());
		categoryResponseDTO.setTotalElements(pageCategories.getTotalElements());
		categoryResponseDTO.setTotalPages(pageCategories.getTotalPages());
		categoryResponseDTO.setLastPage(pageCategories.isLast());
		
		return categoryResponseDTO;
	}

	@Override
	public CategoryDTO updateCategory(Category category, Long categoryId) {
		Category savedCategory = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

		category.setCategoryId(categoryId);

		savedCategory = categoryRepo.save(category);

		return modelMapper.map(savedCategory, CategoryDTO.class);
	}

	@Override
	public String deleteCategory(Long categoryId) {
		Category category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
		
		List<Product> products = category.getProducts();

		products.forEach(product -> {
			productService.deleteProduct(product.getProductId());
		});
		
		categoryRepo.delete(category);

		return "Category with categoryId: " + categoryId + " deleted successfully !!!";
	}

}
