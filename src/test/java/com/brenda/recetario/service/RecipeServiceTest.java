package com.brenda.recetario.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.multipart.MultipartFile;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.exceptions.ImageUploadException;
import com.brenda.recetario.exceptions.InvalidDataException;
import com.brenda.recetario.exceptions.RecipeNotFoundException;
import com.brenda.recetario.models.RecipeCreateDTO;
import com.brenda.recetario.models.RecipeFilteredResponseDTO;
import com.brenda.recetario.models.RecipeResponseDTO;
import com.brenda.recetario.models.RecipeUpdateDTO;
import com.brenda.recetario.repository.RecipeRepository;
import com.brenda.recetario.utils.RecipeTestDataFactory;

import static org.junit.jupiter.api.Assertions.*;

class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------
    // CREATE RECIPE
    // ---------------------------------------------------------
    @Test
    void createRecipe_withValidImage_shouldCreateAndSave() throws Exception {
        RecipeCreateDTO dto = RecipeTestDataFactory.createValidRecipeCreateDTO();
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(imageService.uploadImage(image)).thenReturn("https://image.url/test.jpg");
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.createRecipe(dto, image);

        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals("https://image.url/test.jpg", result.getImageUrl());
        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService).uploadImage(image);
    }

    @Test
    void createRecipe_withoutImage_shouldCreateCorrectly() {
        RecipeCreateDTO dto = RecipeTestDataFactory.createValidRecipeCreateDTO();
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.createRecipe(dto, null);

        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService, never()).uploadImage(any());
    }

    @Test
    void createRecipe_errorWhenUploadImage_shouldThrowInvalidDataException() throws Exception {
        RecipeCreateDTO dto = RecipeTestDataFactory.createValidRecipeCreateDTO();
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(imageService.uploadImage(image)).thenThrow(new ImageUploadException("error"));

        assertThrows(InvalidDataException.class, () -> recipeService.createRecipe(dto, image));
        verify(imageService).uploadImage(image);
    }

    // ---------------------------------------------------------
    // GET RECIPE BY ID
    // ---------------------------------------------------------
    @Test
    void getRecipeById_existing_shouldReturnRecipeResponseDTO() {
        Recipe recipe = RecipeTestDataFactory.createValidRecipeEntity();
        when(recipeRepository.findById("1")).thenReturn(Optional.of(recipe));

        RecipeResponseDTO result = recipeService.getRecipeById("1");

        assertNotNull(result);
        assertEquals(recipe.getId(), result.getId());
        assertEquals(recipe.getTitle(), result.getTitle());
        verify(recipeRepository).findById("1");
    }

    @Test
    void getRecipeById_nonExisting_shouldThrowRecipeNotFoundException() {
        when(recipeRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.getRecipeById("999"));
        verify(recipeRepository).findById("999");
    }

    // ---------------------------------------------------------
    // UPDATE RECIPE
    // ---------------------------------------------------------
    @Test
    void updateRecipe_withNewImage_shouldUpdateAndDeleteOldImage() throws Exception {
        Recipe existingRecipe = RecipeTestDataFactory.createValidRecipeEntity();
        existingRecipe.setImageUrl("https://old-image.url/test.jpg");

        RecipeUpdateDTO updateDTO = RecipeTestDataFactory.createValidRecipeUpdateDTO();
        MultipartFile nuevaImagen = mock(MultipartFile.class);

        when(recipeRepository.findById(updateDTO.getId())).thenReturn(Optional.of(existingRecipe));
        when(nuevaImagen.isEmpty()).thenReturn(false);
        when(imageService.uploadImage(nuevaImagen)).thenReturn("https://new-image.url/test.jpg");
        when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

        Recipe result = recipeService.updateRecipe(updateDTO, nuevaImagen);

        assertNotNull(result);
        assertEquals(updateDTO.getTitle(), result.getTitle());
        assertEquals("https://new-image.url/test.jpg", result.getImageUrl());
        verify(imageService).uploadImage(nuevaImagen);
        verify(imageService).deleteImage("https://old-image.url/test.jpg");
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void updateRecipe_withoutNewImage_shouldUpdateWithoutChangingImage() {
        Recipe existingRecipe = RecipeTestDataFactory.createValidRecipeEntity();
        existingRecipe.setImageUrl("https://old-image.url/test.jpg");

        RecipeUpdateDTO updateDTO = RecipeTestDataFactory.createValidRecipeUpdateDTO();

        when(recipeRepository.findById(updateDTO.getId())).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

        Recipe result = recipeService.updateRecipe(updateDTO, null);

        assertNotNull(result);
        assertEquals("https://old-image.url/test.jpg", result.getImageUrl());
        verify(imageService, never()).uploadImage(any());
        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void updateRecipe_NonExisting_shouldThrowRecipeNotFoundException() {
        RecipeUpdateDTO dto = RecipeTestDataFactory.createValidRecipeUpdateDTO();
        when(recipeRepository.findById(dto.getId())).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.updateRecipe(dto, null));
        verify(recipeRepository).findById(dto.getId());
    }

    // ---------------------------------------------------------
    // DELETE RECIPE
    // ---------------------------------------------------------
    @Test
    void deleteRecipe_withImage_shouldDeleteImageAndRecipe() throws Exception {
        Recipe recipe = RecipeTestDataFactory.createValidRecipeEntity();
        recipe.setImageUrl("https://image.url/test.jpg");

        when(recipeRepository.findById("1")).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe("1");

        verify(imageService).deleteImage("https://image.url/test.jpg");
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteRecipe_withoutImage_shouldDeleteRecipeWithoutDeletingImage() {
        Recipe recipe = RecipeTestDataFactory.createValidRecipeEntity();
        recipe.setImageUrl(null);

        when(recipeRepository.findById("1")).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe("1");

        verify(imageService, never()).deleteImage(any());
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteRecipe_NonExisting_shouldThrowRecipeNotFoundException() {
        when(recipeRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.deleteRecipe("999"));
    }

    // ---------------------------------------------------------
    // SEARCH RECIPES
    // ---------------------------------------------------------
    @Test
    void searchRecipes_withCriteria_shouldReturnPageOfDTOs() {
        Recipe recipe = RecipeTestDataFactory.createValidRecipeEntity();
        List<Recipe> recipes = List.of(recipe);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipes);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(1L);

        Page<RecipeFilteredResponseDTO> result = recipeService.searchRecipes(
                List.of("CENA"), true, "pollo", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(recipe.getTitle(), result.getContent().get(0).getTitle());
        verify(mongoTemplate).find(any(Query.class), eq(Recipe.class));
        verify(mongoTemplate).count(any(Query.class), eq(Recipe.class));
    }
}
