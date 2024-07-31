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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private Long existId;
	private Long noExist;
	private Long dependentId;
	private PageImpl<Product> page;
	private Product product;
	
	@BeforeEach
	void setUp() throws Exception {
		existId=1L;
		noExist=2L;
		dependentId=3L;
		product=Factory.createProduct();
		page= new PageImpl<>(List.of(product));
		
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.findById(existId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(noExist)).thenReturn(Optional.empty());

		
		Mockito.doNothing().when(repository).deleteById(existId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
				
		Mockito.when(repository.existsById(existId)).thenReturn(true);
		Mockito.when(repository.existsById(noExist)).thenReturn(false);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
	}
	
	@Test
	public void deleteShoudDoNothingWhenIdExist() {
		
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
