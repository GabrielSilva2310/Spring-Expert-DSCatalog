package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository catRepository;
	
	private Long existId;
	private Long noExist;
	private Long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private ProductDTO dto;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception {
		existId=1L;
		noExist=2L;
		dependentId=3L;
		product=Factory.createProduct();
		category=Factory.createCategory();
		page= new PageImpl<>(List.of(product));
		dto=new ProductDTO(product);
		
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.findById(existId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(noExist)).thenReturn(Optional.empty());
		
		Mockito.when(repository.getReferenceById(existId)).thenReturn(product);
		Mockito.when(repository.getReferenceById(noExist)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(catRepository.getReferenceById(existId)).thenReturn(category);
		Mockito.when(catRepository.getReferenceById(noExist)).thenThrow(EntityNotFoundException.class);

		
		Mockito.doNothing().when(repository).deleteById(existId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
				
		Mockito.when(repository.existsById(existId)).thenReturn(true);
		Mockito.when(repository.existsById(noExist)).thenReturn(false);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
	}
	
	@Test
	public void findAllShouldReturnPage() {
		Pageable pageable=PageRequest.of(0, 10);
		
		Page<ProductDTO> result=service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository).findAll(pageable);
		
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenExistId() {
		
		ProductDTO result=service.findById(existId);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).findById(existId);
		
	}
	
	@Test
	public void findByIdShoulThrowResourceNotFoundExceptionWhenDoesNotExistId() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.findById(noExist);
		});
		
	
	}
	
	
	@Test
	public void updateShouldReturnProductDtoWhenExistId() {
	
		ProductDTO updateProduct=service.update(existId, dto);
		
		Assertions.assertNotNull(updateProduct);
		
		Mockito.verify(repository, Mockito.times(1)).getReferenceById(existId);
		Mockito.verify(repository, Mockito.times(1)).save(product);

	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenDoesNotExistId() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.update(noExist, dto);
		});
		
		Mockito.verify(repository, Mockito.times(1)).getReferenceById(noExist);

	}
	
	
	@Test
	public void deleteShoudDoNothingWhenExistId() {
		
		Assertions.assertDoesNotThrow(()->
		service.delete(existId));
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existId);
		
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenDoesNotExistId() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.delete(noExist);
		});
		
		Mockito.verify(repository, Mockito.times(1)).existsById(noExist);
		
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, ()->{
			service.delete(dependentId);
		});
		
	}
	
	
	

}
