package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.CategoryIsNotEmptyException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        checkCategoryName(newCategoryDto.getName());
        Category category = categoryRepository.save(categoryMapper.toCategory(newCategoryDto));
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryOrThrow(categoryId);
        checkCategoryByEvents(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = getCategoryOrThrow(categoryId);
        if (!(category.getName().equals(newCategoryDto.getName()))) {
            checkCategoryName(newCategoryDto.getName());
        }
        category.setName(newCategoryDto.getName());
        Category updatedCategory = categoryRepository.save(categoryRepository.save(category));
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        int pageNumber = from / size;
        Page<Category> categoryPage = categoryRepository.findAll(PageRequest.of(pageNumber, size));
        return categoryPage.map(categoryMapper::toCategoryDto).getContent();
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = getCategoryOrThrow(categoryId);
        return categoryMapper.toCategoryDto(category);
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
    }

    private void checkCategoryByEvents(Long categoryId) {
        List<Event> events = eventRepository.findByCategory_Id(categoryId);
        if (!events.isEmpty()) {
            throw new CategoryIsNotEmptyException("The category is not empty");
        }
    }

    private void checkCategoryName(String name) {
        Optional<Category> category = categoryRepository.getCategoryByName(name);
        if (category.isPresent()) {
            throw new CategoryIsNotEmptyException("The category name already exists");
        }
    }
}
