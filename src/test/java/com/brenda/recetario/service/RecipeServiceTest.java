package com.brenda.recetario.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
import com.brenda.recetario.enums.RecipeCategory;
import com.brenda.recetario.exceptions.InvalidDataException;
import com.brenda.recetario.exceptions.RecipeNotFoundException;
import com.brenda.recetario.models.RecipeCreateDTO;
import com.brenda.recetario.models.RecipeFilteredResponseDTO;
import com.brenda.recetario.models.RecipeResponseDTO;
import com.brenda.recetario.models.RecipeUpdateDTO;
import com.brenda.recetario.repository.RecipeRepository;
import com.brenda.recetario.utils.TestDataFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RecipeServiceTest {

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

    @Test
    void givenValidDtoAndImage_whenCreateRecipe_thenRecipeIsSavedWithImageUrl() throws Exception {
        RecipeCreateDTO dto = TestDataFactory.createRecipeCreateDTO();
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(imageService.uploadImage(image)).thenReturn("http://img.com/pizza.png");

        Recipe recipe = recipeService.createRecipe(dto, image);

        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService).uploadImage(image);
        assertThat(recipe.getTitle()).isEqualTo("Pizza");
        assertThat(recipe.getImageUrl()).isEqualTo("http://img.com/pizza.png");
    }

    @Test
    void givenValidDtoWithoutImage_whenCreateRecipe_thenRecipeIsSavedWithoutImageUrl() {
        RecipeCreateDTO dto = TestDataFactory.createRecipeCreateDTO();

        Recipe recipe = recipeService.createRecipe(dto, null);

        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService, never()).uploadImage(any());
        assertThat(recipe.getImageUrl()).isNull();
    }

    @Test
    void givenErrorWhenSavingRecipe_thenDeletesUploadedImageAndThrowsException() throws Exception {
        RecipeCreateDTO dto = TestDataFactory.createRecipeCreateDTO();
        MultipartFile image = mock(MultipartFile.class);

        when(image.isEmpty()).thenReturn(false);
        when(imageService.uploadImage(image)).thenReturn("http://img.com/pizza.png");
        doThrow(new RuntimeException("DB error")).when(recipeRepository).save(any(Recipe.class));

        assertThatThrownBy(() -> recipeService.createRecipe(dto, image))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Error creando la receta");

        verify(imageService).deleteImage("http://img.com/pizza.png");
    }

    @Test
    void givenExistingRecipeId_whenGetRecipeById_thenReturnRecipeResponseDTO() {
        Recipe recipe = TestDataFactory.createRecipe();
        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        RecipeResponseDTO result = recipeService.getRecipeById("123");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("123");
        assertThat(result.getTitle()).isEqualTo("Pizza");
        verify(recipeRepository).findById("123");
    }

    @Test
    void givenNonExistingRecipeId_whenGetRecipeById_thenThrowException() {
        when(recipeRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.getRecipeById("999"))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("La receta especificada no existe.");
    }

    @Test
    void givenValidDtoAndImage_whenUpdateRecipe_thenRecipeIsUpdatedWithImageUrl() throws Exception {
        Recipe recipe = TestDataFactory.createRecipe();
        RecipeUpdateDTO dto = TestDataFactory.createRecipeUpdateDTO();
        MultipartFile image = mock(MultipartFile.class);

        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));
        when(image.isEmpty()).thenReturn(false);
        when(imageService.uploadImage(image)).thenReturn("http://img.com/pizza.png");

        recipe = recipeService.updateRecipe(dto, image);

        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService).uploadImage(image);
        verify(imageService).deleteImage("http://test.com/cheesepizza.jpg");
        assertThat(recipe.getImageUrl()).isEqualTo("http://img.com/pizza.png");
    }

    @Test
    void givenValidDtoWithoutImage_whenUpdateRecipe_thenRecipeIsUpdatedWithoutImageChange() {
        Recipe recipe = TestDataFactory.createRecipe();
        RecipeUpdateDTO dto = TestDataFactory.createRecipeUpdateDTO();

        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        recipe = recipeService.updateRecipe(dto, null);

        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService, never()).uploadImage(any());
        verify(imageService, never()).deleteImage(any());
    }

    @Test
    void givenNonExistingRecipeId_whenUpdateRecipe_thenThrowException() {
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setId("999");
        when(recipeRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(dto, null))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("La receta especificada no existe.");
    }

    @Test
    void givenValidRecipeIdWithImage_whenDeleteRecipe_thenRecipeAndImageDeleted() {
        Recipe recipe = TestDataFactory.createRecipe();
        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe("123");

        verify(imageService).deleteImage("http://test.com/cheesepizza.jpg");
        verify(recipeRepository).delete(recipe);
    }

    @Test
    void givenValidRecipeIdWithoutImage_whenDeleteRecipe_thenOnlyRecipeDeleted() {
        Recipe recipe = TestDataFactory.createRecipeWithoutImage();
        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe("123");

        verify(recipeRepository).delete(recipe);
        verify(imageService, never()).deleteImage(any());
    }

    @Test
    void givenNonExistingRecipeId_whenDeleteRecipe_thenThrowException() {
        when(recipeRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteRecipe("999"))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("La receta especificada no existe.");
    }

    @Test
    void givenNoFilters_whenSearchRecipes_thenReturnAllPaged() {
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(1L);

        Page<RecipeFilteredResponseDTO> result = recipeService.searchRecipes(null, null, null, 0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Pizza");
    }

    @Test
    void givenCategoryFilter_whenSearchRecipes_thenReturnMatchingRecipes() {
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(1L);

        Page<RecipeFilteredResponseDTO> result = recipeService.searchRecipes(List.of("CENA"), null, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategories())
                .contains(RecipeCategory.CENA);
    }

    @Test
    void givenFitFilter_whenSearchRecipes_thenReturnFilteredByFit() {
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(1L);

        Page<RecipeFilteredResponseDTO> result = recipeService.searchRecipes(null, true, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFit()).isTrue();
    }

    @Test
    void givenSearchTerm_whenSearchRecipes_thenReturnMatchingResults() {
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(1L);

        Page<RecipeFilteredResponseDTO> result = recipeService.searchRecipes(null, null, "pizza", 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).containsIgnoringCase("pizza");
    }
}
