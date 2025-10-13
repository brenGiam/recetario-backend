package com.brenda.recetario.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;
import com.brenda.recetario.models.RecipeCreateDTO;
import com.brenda.recetario.models.RecipeFilteredResponseDTO;
import com.brenda.recetario.models.RecipeResponseDTO;
import com.brenda.recetario.models.RecipeUpdateDTO;
import com.brenda.recetario.service.RecipeService;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/recipes")
@Tag(name = "Recetas", description = "Operaciones relacionadas con las recetas del sistema")
public class RecipeController {
    private final RecipeService recipeService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Operation(summary = "Crear una nueva receta", description = "Crea una receta a partir de los datos enviados en formato JSON y una imagen opcional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Receta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRecipe(
            @Parameter(description = "Datos de la receta en formato JSON") @RequestPart("recipe") String recipeJson,
            @Parameter(description = "Imagen opcional de la receta") @RequestPart(value = "image", required = false) MultipartFile image)
            throws JsonProcessingException {
        log.info("RecipeController: Creando nueva receta...");

        RecipeCreateDTO dto = objectMapper.readValue(recipeJson, RecipeCreateDTO.class);

        Set<ConstraintViolation<RecipeCreateDTO>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage));
            log.warn("RecipeController: Error de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        Recipe recipe = recipeService.createRecipe(dto, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RecipeResponseDTO(recipe));
    }

    @Operation(summary = "Obtener una receta", description = "Devuelve los detalles de una receta mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta encontrada"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(
            @Parameter(description = "ID de la receta a buscar") @PathVariable String id) {
        log.info("RecipeController: Buscando receta con id: {}", id);
        RecipeResponseDTO dto = recipeService.getRecipeById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Actualizar una receta", description = "Permite modificar uno o varios campos de una receta existente, incluyendo su imágen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o JSON incorrecto"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRecipe(
            @Parameter(description = "Datos actualizados de la receta en formato JSON") @RequestPart("recipe") String recipeJson,
            @Parameter(description = "Nueva imagen opcional de la receta") @RequestPart(value = "image", required = false) MultipartFile image)
            throws JsonProcessingException {

        log.info("RecipeController: Actualizando receta...");

        RecipeUpdateDTO dto = objectMapper.readValue(recipeJson, RecipeUpdateDTO.class);

        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage));
            log.warn("RecipeController: Error de validación: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        Recipe recipe = recipeService.updateRecipe(dto, image);

        return ResponseEntity.status(HttpStatus.OK).body(new RecipeResponseDTO(recipe));
    }

    @Operation(summary = "Eliminar una receta", description = "Busca y elimina una receta mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(
            @Parameter(description = "ID de la receta a eliminar") @PathVariable String id) {

        log.info("RecipeController: Eliminando receta con id: {}", id);
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok(Map.of("message", "Receta eliminada exitosamente"));
    }

    @Operation(summary = "Obtener todas las recetas con o sin filtros", description = "Devuelve una lista paginada de recetas que pueden estar filtradas por categoría y/o si son fit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/filter")
    public ResponseEntity<?> getRecipesWithFilter(
            @Parameter(description = "Categoría de la receta") @RequestParam(required = false) RecipeCategory category,
            @Parameter(description = "Si la receta es fit o no") @RequestParam(required = false) Boolean fit,
            @Parameter(description = "Número de página (0 por defecto)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página (10 por defecto)") @RequestParam(defaultValue = "10") int size) {

        log.info("RecipeController: Obteniendo recetas con filtros -> category: {}, fit: {}", category, fit);
        Page<RecipeFilteredResponseDTO> recipes = recipeService.getAllRecipesWithFilter(category, fit, page, size);
        return ResponseEntity.ok(recipes);
    }

    @Operation(summary = "Obtener todas las recetas con ingredientes específicos", description = "Devuelve una lista paginada de recetas filtradas por ingredientes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/ingredients")
    public ResponseEntity<?> getRecipesByIngredients(
            @Parameter(description = "Lista de ingredientes a buscar") @RequestParam List<String> ingredients,
            @Parameter(description = "Número de página (0 por defecto)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página (10 por defecto)") @RequestParam(defaultValue = "10") int size) {

        log.info("RecipeController: Buscando recetas con ingredientes: {}", ingredients);
        Page<RecipeFilteredResponseDTO> recipes = recipeService.getAllRecipesWithIngredients(ingredients, page,
                size);
        return ResponseEntity.ok(recipes);
    }
}