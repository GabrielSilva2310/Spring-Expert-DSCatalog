package com.devsuperior.dscatalog.services;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
public class ProductServiceIT {
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private Long existId;
	private Long noExistId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		
		existId=1L;
		noExistId=26L;
		countTotalProducts=25L;
	
	}
	
	@Test
	public void deleteShouldDeleteWhenExistId() {
		service.delete(existId);
		
		Assertions.assertEquals(countTotalProducts-1, repository.count());
		
	}
	
	@Test
	public void deleteShouldThrownResourceNotFoundExceptionWhenDoesNotExistId() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.delete(noExistId);
		});
		
	}
	
	
	
	

}
