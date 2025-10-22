package com.brenda.recetario.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.brenda.recetario.entity.Recipe;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
}
