package com.service.productorder.services;

import com.service.productorder.entites.Category;
import com.service.productorder.payloads.CategoryDTO;
import com.service.productorder.payloads.CategoryResponse;

public interface CategoryService {

	CategoryDTO createCategory(Category category);

	CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO updateCategory(Category category, Long categoryId);

	String deleteCategory(Long categoryId);
}
