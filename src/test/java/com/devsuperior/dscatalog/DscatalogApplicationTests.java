package com.devsuperior.dscatalog;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;

@DataJpaTest
class DscatalogApplicationTests {
	
	@Autowired
	private ProductRepository repository;
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		Long existsId=5L;
		
		repository.deleteById(existsId);
		Optional<Product> result=repository.findById(existsId);
		
		Assertions.assertFalse(result.isPresent());
		
	}
	

}
