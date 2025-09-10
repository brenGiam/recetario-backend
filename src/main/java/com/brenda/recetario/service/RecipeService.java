package com.brenda.recetario.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;
import com.brenda.recetario.models.RecipeCreateDTO;
import com.brenda.recetario.models.RecipeFilteredResponseDTO;
import com.brenda.recetario.models.RecipeResponseDTO;
import com.brenda.recetario.models.RecipeUpdateDTO;
import com.brenda.recetario.repository.RecipeRepository;

import lombok.Data;

@Service
@Data
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final ImageService imageService;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public Recipe createRecipe(RecipeCreateDTO recipeDTO, MultipartFile image) {
        Recipe recipe = new Recipe();

        recipe.setTitle(recipeDTO.getTitle());
        recipe.setCategory(recipeDTO.getCategory());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setFit(recipeDTO.getFit());

        String imageUrl = null;

        try {
            if (image != null && !image.isEmpty()) {
                imageUrl = imageService.uploadImage(image);
                recipe.setImageUrl(imageUrl);
            }

            recipeRepository.save(recipe);
            return recipe;

        } catch (Exception e) {
            if (imageUrl != null) {
                imageService.deleteImage(imageUrl);
            }
            throw new RuntimeException("Error creando la receta", e);
        }
    }

    @Transactional(readOnly = true)
    public RecipeResponseDTO getRecipeById(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La receta no existe"));

        return new RecipeResponseDTO(recipe);
    }

    @Transactional
    public Recipe updateRecipe(RecipeUpdateDTO recipeDTO, MultipartFile image) {
        Recipe recipe = recipeRepository.findById(recipeDTO.getId())
                .orElseThrow(() -> new RuntimeException("La receta especificada no existe."));

        recipe.setTitle(recipeDTO.getTitle());
        recipe.setCategory(recipeDTO.getCategory());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setFit(recipeDTO.getFit());

        try {
            if (image != null && !image.isEmpty()) {
                String oldImageUrl = recipe.getImageUrl();
                String newImageUrl = imageService.uploadImage(image);
                recipe.setImageUrl(newImageUrl);

                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    imageService.deleteImage(oldImageUrl);
                }
            }
            recipeRepository.save(recipe);
            return recipe;
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando la receta", e);
        }
    }

    @Transactional
    public void deleteRecipe(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La receta especificada no existe."));

        if (recipe.getImageUrl() != null) {
            try {
                imageService.deleteImage(recipe.getImageUrl());
            } catch (Exception e) {
                System.err.println("No se pudo eliminar la imagen: " + e.getMessage());
            }
        }

        recipeRepository.delete(recipe);
    }

    @Transactional(readOnly = true)
    public Page<RecipeFilteredResponseDTO> getAllRecipesWithFilter(
            RecipeCategory category,
            Boolean fit,
            int page,
            int size) {

        Query query = new Query();

        if (category != null) {
            query.addCriteria(Criteria.where("category").is(category));
        }
        if (fit != null) {
            query.addCriteria(Criteria.where("fit").is(fit));
        }

        Pageable pageable = PageRequest.of(page, size);
        query.with(pageable);

        List<Recipe> recipes = mongoTemplate.find(query, Recipe.class);

        long total = mongoTemplate.count(query.skip(-1).limit(-1), Recipe.class);

        List<RecipeFilteredResponseDTO> dtos = recipes.stream()
                .map(RecipeFilteredResponseDTO::new)
                .toList();

        return new PageImpl<>(dtos, pageable, total);

    }

    @Transactional(readOnly = true)
    public Page<RecipeFilteredResponseDTO> getAllRecipesWithIngredients(
            List<String> ingredients,
            int page,
            int size) {

        Query query = new Query();

        if (ingredients != null && !ingredients.isEmpty()) {
            query.addCriteria(Criteria.where("ingredients").all(ingredients));
        }

        Pageable pageable = PageRequest.of(page, size);
        query.with(pageable);

        List<Recipe> recipes = mongoTemplate.find(query, Recipe.class);

        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Recipe.class);

        List<RecipeFilteredResponseDTO> dtos = recipes.stream()
                .map(RecipeFilteredResponseDTO::new)
                .toList();

        return new PageImpl<>(dtos, pageable, total);
    }
}
