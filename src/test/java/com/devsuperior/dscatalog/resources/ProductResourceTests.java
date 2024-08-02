package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	private Long existId;
	private Long noExistId;
	
	
	@BeforeEach
	void setUp() throws Exception {
		existId=1L;
		noExistId=2L;
		
		productDTO=Factory.createProductDTO();
		page=new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any())).thenReturn(page);
		
		when(service.findById(existId)).thenReturn(productDTO);
		when(service.findById(noExistId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.update(eq(existId), any())).thenReturn(productDTO);
		when(service.update(eq(noExistId), any())).thenThrow(ResourceNotFoundException.class);

		
	}
	
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions result=
				mockMvc.perform(get("/products")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
				
	}
	
	
	@Test
	public void findByIdShouldReturnProductDtoWhenExistId() throws Exception {
		ResultActions result=
				mockMvc.perform(get("/products/{id}", existId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
				
	}
	
	public void findByIdShoulNotFoundWhenDoesNotExistId() throws Exception {
		
		ResultActions result=
				mockMvc.perform(get("/products/{id}", noExistId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDtoWhenExistId() throws Exception {
		
		String JsonBody=objectMapper.writeValueAsString(productDTO);
		
		ResultActions result=
				mockMvc.perform(put("/products/{id}", existId)
				.content(JsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
				
	}
	
	@Test
	public void updateShouldNotFoundWhenDoesNotExistId() throws Exception {
		
		String JsonBody=objectMapper.writeValueAsString(productDTO);

		ResultActions result=
				mockMvc.perform(put("/products/{id}", noExistId)
						.content(JsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
				
		
	}

}
