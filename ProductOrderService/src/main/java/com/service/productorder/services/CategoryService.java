package com.service.productorder.services;

import com.service.productorder.entites.Category;
import com.service.productorder.dtos.CategoryDTO;
import com.service.productorder.dtos.CategoryResponseDTO;

public interface CategoryService {

	CategoryDTO createCategory(Category category);

	CategoryResponseDTO getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO updateCategory(Category category, Long categoryId);

	String deleteCategory(Long categoryId);
}
