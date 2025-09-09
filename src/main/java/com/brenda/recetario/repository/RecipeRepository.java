package com.brenda.recetario.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.brenda.recetario.model.Recipe;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    List<Recipe> findByCategory(String category);

    List<Recipe> findByisFit(boolean isFit);

    List<Recipe> findByIngredientContaining(String ingredient);

    List<Recipe> findByIngredientsAll(List<String> ingredients);

    List<Recipe> findByTitleContainingIgnoreCase(String title);
}
