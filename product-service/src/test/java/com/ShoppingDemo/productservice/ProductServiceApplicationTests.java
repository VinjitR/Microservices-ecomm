package com.ShoppingDemo.productservice;

import com.ShoppingDemo.productservice.dto.ProductRequest;
import com.ShoppingDemo.productservice.dto.ProductResponse;
import com.ShoppingDemo.productservice.model.Product;
import com.ShoppingDemo.productservice.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5");
	@Autowired
	private MockMvc mockmvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateProduct() throws Exception {
		createProductTest("iphone12", 800);
		Assertions.assertEquals(2,productRepository.findAll().size());
	}




	@Test
	void shouldVerifyProduct() throws Exception{
		createProductTest("iphone14", 1200);
		mockmvc.perform(MockMvcRequestBuilders.get("/api/product"))
				.andExpect(status().isOk());
		List<ProductResponse> productResponses = getProductResponses();
		Assertions.assertTrue(productResponses.size()==1);
		for(int i=0;i<productResponses.size();i++){
			System.out.println(productResponses.get(i).getName()+productResponses.get(i).getPrice());
		}
	}

	private void createProductTest(String name, int value) throws Exception {
		ProductRequest productRequest= getProductRequest(name,value);
		String productRequestString=objectMapper.writeValueAsString(productRequest);
		mockmvc.perform(MockMvcRequestBuilders.post("/api/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestString))
				.andExpect(status().isCreated());
	}

	private List<ProductResponse> getProductResponses() {
		List<Product> products = productRepository.findAll();
		return products.stream().map(this::mapToProductResponse).toList();
	}

	private ProductRequest getProductRequest(String name, int value) {
		return ProductRequest.builder()
				.name(name)
				.description(name)
				.price(BigDecimal.valueOf(value))
				.build();
	}
	private ProductResponse mapToProductResponse(Product product) {
		return ProductResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.build();
	}

}
