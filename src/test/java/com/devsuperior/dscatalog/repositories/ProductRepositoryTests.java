package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	Long existsId;
	Long countTotalProduct;
	
	@BeforeEach
	void setUp() throws Exception {
		existsId=5L;
		countTotalProduct=25L;
	}
	
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existsId);
		Optional<Product> result=repository.findById(existsId);
		
		Assertions.assertFalse(result.isPresent());
		
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		Product product=Factory.createProduct();
		product.setId(null);
		product=repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProduct+1, product.getId());
		
	}
	
}
