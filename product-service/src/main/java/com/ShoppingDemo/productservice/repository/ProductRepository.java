package com.ShoppingDemo.productservice.repository;

import com.ShoppingDemo.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product,String> {
}
