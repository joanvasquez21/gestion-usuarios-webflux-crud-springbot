package com.springboot.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.springboot.webflux.app.models.documents.Categoria;

public interface CategoriaDAO extends ReactiveMongoRepository<Categoria, String> {

}
