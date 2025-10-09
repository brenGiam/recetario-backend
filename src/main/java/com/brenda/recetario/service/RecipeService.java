package com.brenda.recetario.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Objects;

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
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
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
        recipe.setIngredients(ingredientsNormalitation(recipeDTO.getIngredients()));
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setFit(recipeDTO.getFit());

        String imageUrl = null;

        try {
            if (image != null && !image.isEmpty()) {
                imageUrl = imageService.uploadImage(image);
                recipe.setImageUrl(imageUrl);
                log.info("Servicio: Imagen subida correctamente: {}", imageUrl);
            }

            recipeRepository.save(recipe);
            log.info("Servicio: Receta creada correctamente: {}", recipe.getTitle());
            return recipe;

        } catch (Exception e) {
            log.error("Servicio: Error creando la receta: {}", recipe.getTitle(), e);
            if (imageUrl != null) {
                imageService.deleteImage(imageUrl);
                log.warn("Servicio: Se eliminó la imagen subida por el error: {}", imageUrl);
            }
            throw new RuntimeException("Servicio: Error creando la receta", e);
        }
    }

    public RecipeResponseDTO getRecipeById(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio: La receta especificada no existe."));

        return new RecipeResponseDTO(recipe);
    }

    @Transactional
    public Recipe updateRecipe(RecipeUpdateDTO recipeDTO, MultipartFile image) {
        Recipe recipe = recipeRepository.findById(recipeDTO.getId())
                .orElseThrow(() -> new RuntimeException("Servicio: La receta especificada no existe."));

        recipe.setTitle(recipeDTO.getTitle());
        recipe.setCategory(recipeDTO.getCategory());
        recipe.setIngredients(ingredientsNormalitation(recipeDTO.getIngredients()));
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setFit(recipeDTO.getFit());

        try {
            if (image != null && !image.isEmpty()) {
                String oldImageUrl = recipe.getImageUrl();
                String newImageUrl = imageService.uploadImage(image);
                log.info("Servicio: Nueva imagen subida correctamente: {}", newImageUrl);
                recipe.setImageUrl(newImageUrl);

                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    imageService.deleteImage(oldImageUrl);
                    log.info("Servicio: Imagen anterior eliminada correctamente: {}", oldImageUrl);
                }
            }
            recipeRepository.save(recipe);
            log.info("Servicio: Receta actualizada correctamente: {}", recipe.getTitle());
            return recipe;
        } catch (Exception e) {
            log.error("Servicio: Error actualizando la receta: {}", recipe.getTitle(), e);
            throw new RuntimeException("Servicio: Error actualizando la receta", e);
        }
    }

    @Transactional
    public void deleteRecipe(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio: La receta especificada no existe."));

        if (recipe.getImageUrl() != null) {
            try {
                imageService.deleteImage(recipe.getImageUrl());
                log.info("Servicio: Imagen eliminada exitosamente: {}", recipe.getImageUrl());
            } catch (Exception e) {
                log.error("Servicio: No se pudo eliminar la imagen: {}", recipe.getImageUrl(), e);
            }
        }

        recipeRepository.delete(recipe);
        log.info("Servicio: Receta eliminada exitosamente: {}");
    }

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

    public Page<RecipeFilteredResponseDTO> getAllRecipesWithIngredients(
            List<String> ingredients,
            int page,
            int size) {

        Query query = new Query();

        if (ingredients != null && !ingredients.isEmpty()) {
            List<Criteria> regexCriteria = ingredients.stream()
                    .map(ing -> Criteria.where("ingredients").regex(".*" + ing + ".*", "i"))
                    .toList();

            query.addCriteria(new Criteria().andOperator(regexCriteria.toArray(new Criteria[0])));
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

    // Auxiliary methods
    public List<String> ingredientsNormalitation(List<String> ingredients) {
        if (ingredients == null)
            return List.of();

        return ingredients.stream()
                .filter(Objects::nonNull) // avoid nulls
                .map(String::trim) // delete extra spaces
                .map(String::toLowerCase)
                .map(this::removeAccents)
                .toList();
    }

    private String removeAccents(String input) {
        return Normalizer
                .normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");// remove accents
    }
}
