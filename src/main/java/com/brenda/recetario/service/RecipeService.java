package com.brenda.recetario.service;

import java.text.Normalizer;
import java.util.ArrayList;
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
import com.brenda.recetario.exceptions.ImageDeletionException;
import com.brenda.recetario.exceptions.ImageUploadException;
import com.brenda.recetario.exceptions.InvalidDataException;
import com.brenda.recetario.exceptions.RecipeNotFoundException;
import com.brenda.recetario.models.RecipeCreateDTO;
import com.brenda.recetario.models.RecipeFilteredResponseDTO;
import com.brenda.recetario.models.RecipeResponseDTO;
import com.brenda.recetario.models.RecipeUpdateDTO;
import com.brenda.recetario.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final ImageService imageService;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public Recipe createRecipe(RecipeCreateDTO recipeDTO, MultipartFile image) {
        Recipe recipe = new Recipe();

        recipe.setTitle(recipeDTO.getTitle());
        recipe.setCategories(recipeDTO.getCategories());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setFit(recipeDTO.getFit());

        // Normalization to use in search methods
        recipe.setNormalizedTitle(removeAccents(recipeDTO.getTitle().toLowerCase()));
        recipe.setNormalizedIngredients(normalizeIngredientsList(recipeDTO.getIngredients()));

        String imageUrl = null;

        try {
            if (image != null && !image.isEmpty()) {
                imageUrl = imageService.uploadImage(image);
                recipe.setImageUrl(imageUrl);
                log.info("RecipeService: Imagen subida correctamente: {}", imageUrl);
            }

            recipeRepository.save(recipe);
            log.info("RecipeService: Receta creada correctamente: {}", recipe.getTitle());
            return recipe;

        } catch (ImageUploadException e) {
            log.error("RecipeService: Error subiendo imagen", e);
            throw new InvalidDataException("No se pudo subir la imagen de la receta", e);
        } catch (Exception e) {
            log.error("RecipeService: Error creando la receta: {}", recipe.getTitle(), e);
            if (imageUrl != null) {
                try {
                    imageService.deleteImage(imageUrl);
                    log.warn("RecipeService: Imagen eliminada por error: {}", imageUrl);
                } catch (ImageDeletionException ex) {
                    log.error("RecipeService: No se pudo eliminar la imagen luego del error", ex);
                }
            }
            throw new InvalidDataException("Error creando la receta", e);
        }
    }

    public RecipeResponseDTO getRecipeById(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("RecipeService: No se encontró receta con ID: {}", id);
                    return new RecipeNotFoundException("La receta especificada no existe.");
                });

        log.info("RecipeService: Receta encontrada: {}", recipe.getTitle());
        return new RecipeResponseDTO(recipe);
    }

    @Transactional
    public Recipe updateRecipe(RecipeUpdateDTO recipeDTO, MultipartFile image) {
        Recipe recipe = recipeRepository.findById(recipeDTO.getId())
                .orElseThrow(() -> new RecipeNotFoundException("La receta especificada no existe."));

        recipe.setTitle(recipeDTO.getTitle());
        recipe.setCategories(recipeDTO.getCategories());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setFit(recipeDTO.getFit());

        // Normalization to use in search methods
        recipe.setNormalizedTitle(removeAccents(recipeDTO.getTitle().toLowerCase()));
        recipe.setNormalizedIngredients(normalizeIngredientsList(recipeDTO.getIngredients()));

        try {
            if (image != null && !image.isEmpty()) {
                String oldImageUrl = recipe.getImageUrl();
                String newImageUrl = imageService.uploadImage(image);
                log.info("RecipeService: Nueva imagen subida correctamente: {}", newImageUrl);
                recipe.setImageUrl(newImageUrl);

                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                    try {
                        imageService.deleteImage(oldImageUrl);
                        log.info("RecipeService: Imagen anterior eliminada: {}", oldImageUrl);
                    } catch (ImageDeletionException ex) {
                        log.error("RecipeService: No se pudo eliminar la imagen anterior", ex);
                    }
                }
            }
            recipeRepository.save(recipe);
            log.info("RecipeService: Receta actualizada correctamente: {}", recipe.getTitle());
            return recipe;
        } catch (ImageUploadException e) {
            log.error("RecipeService: Error subiendo imagen", e);
            throw new InvalidDataException("No se pudo subir la nueva imagen", e);
        } catch (Exception e) {
            log.error("RecipeService: Error actualizando la receta: {}", recipe.getTitle(), e);
            throw new InvalidDataException("Error actualizando la receta: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteRecipe(String id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("RecipeService: No se encontró receta con ID: {}", id);
                    return new RecipeNotFoundException("La receta especificada no existe.");
                });

        if (recipe.getImageUrl() != null) {
            try {
                imageService.deleteImage(recipe.getImageUrl());
                log.info("RecipeService: Imagen eliminada: {}", recipe.getImageUrl());
            } catch (ImageDeletionException e) {
                log.error("RecipeService: No se pudo eliminar la imagen", e);
            }
        }

        recipeRepository.delete(recipe);
        log.info("RecipeService: Receta eliminada exitosamente: {}", recipe.getTitle());
    }

    public Page<RecipeFilteredResponseDTO> searchRecipes(
            List<String> categories,
            Boolean fit,
            String search,
            int page,
            int size) {

        Query query = new Query();

        List<Criteria> criteriaList = new ArrayList<>();

        if (categories != null && !categories.isEmpty()) {
            criteriaList.add(Criteria.where("categories").in(categories));
        }

        if (fit != null) {
            criteriaList.add(Criteria.where("fit").is(fit));
        }

        if (search != null && !search.isBlank()) {
            String[] keywords = search.trim().split("\\s+");
            List<Criteria> keywordCriteria = new ArrayList<>();

            for (String keyword : keywords) {
                keywordCriteria.add(new Criteria().orOperator(
                        Criteria.where("title").regex(".*" + keyword + ".*", "i"),
                        Criteria.where("ingredients").regex(".*" + keyword + ".*", "i")));
            }

            // Recipes that contain all the words:
            criteriaList.add(new Criteria().andOperator(keywordCriteria.toArray(new Criteria[0])));

            // Recipes that contain at least one word:
            // criteriaList.add(new Criteria().orOperator(keywordCriteria.toArray(new
            // Criteria[0])));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
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

    // Auxiliary method
    private List<String> normalizeIngredientsList(List<String> ingredients) {
        if (ingredients == null)
            return List.of();
        return ingredients.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .map(this::removeAccents)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private String removeAccents(String input) {
        if (input == null)
            return "";
        return Normalizer
                .normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // remove accents
    }
}
