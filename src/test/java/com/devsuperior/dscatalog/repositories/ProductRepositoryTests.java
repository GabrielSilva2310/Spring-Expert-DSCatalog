package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devsuperior.dscatalog.entities.Product;

@DataJpaTest
class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	Long existsId;
	
	@BeforeEach
	void setUp() throws Exception {
		existsId=5L;
	}
	
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existsId);
		Optional<Product> result=repository.findById(existsId);
		
		Assertions.assertFalse(result.isPresent());
		
	}
	
}
