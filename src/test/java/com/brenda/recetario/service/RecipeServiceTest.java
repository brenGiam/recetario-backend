package com.brenda.recetario.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
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
import com.brenda.recetario.exceptions.ImageDeletionException;
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

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidDtoAndImage_whenCreateRecipe_thenRecipeIsSavedWithImageUrl() throws Exception {
        // Given
        RecipeCreateDTO dto = TestDataFactory.createRecipeCreateDTO();

        MultipartFile image = mock(MultipartFile.class); // Creates a Multipartfile mock for not to upload a real file
        when(image.isEmpty()).thenReturn(false); // Simulates that the file exists and it is not empty
        when(imageService.uploadImage(image)).thenReturn("http://img.com/pizza.png"); // When the image service is
                                                                                      // called with this mock, returns
                                                                                      // the URL that I'm gonna use in
                                                                                      // the assertions

        // When
        Recipe recipe = recipeService.createRecipe(dto, image);

        // Then
        verify(recipeRepository).save(any(Recipe.class)); // Verifies that "save" was called
        verify(imageService).uploadImage(image); // Verifies that tried to upload the image
        assertThat(recipe.getTitle()).isEqualTo("Pizza"); // Verifies the result
        assertThat(recipe.getImageUrl()).isEqualTo("http://img.com/pizza.png");// Verifies the result
    }

    @Test
    void givenValidDtoWithoutImage_whenCreateRecipe_thenRecipeIsSavedWithoutImageUrl() {
        // Given
        RecipeCreateDTO dto = TestDataFactory.createRecipeCreateDTO();

        // When
        Recipe recipe = recipeService.createRecipe(dto, null); // Image null, the method doesn't have to call to upload
                                                               // service

        // Then
        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService, never()).uploadImage(any()); // Verifies that upload method wasn't called
        assertThat(recipe.getImageUrl()).isNull(); // Verifies that the URL is null
    }

    @Test
    void givenErrorWhenSavingRecipe_thenDeletesUploadedImageAndThrowsException() throws Exception {
        // Given
        RecipeCreateDTO dto = TestDataFactory.createRecipeCreateDTO();

        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(imageService.uploadImage(image)).thenReturn("http://img.com/pizza.png");

        doThrow(new RuntimeException("DB error")).when(recipeRepository).save(any(Recipe.class)); // Simulates an DB
                                                                                                  // error and throws an
                                                                                                  // exception

        // When + then
        assertThatThrownBy(() -> recipeService.createRecipe(dto, image))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error creando la receta"); // Verifies that error was thrown with the correct
                                                                  // message

        verify(imageService).deleteImage("http://img.com/pizza.png"); // Verifies that in catch part, the service
                                                                      // tried to delete the image that was uploaded
    }

    @Test
    void givenExisgtingRecipeId_whenGetRecipeById_thenReturnRecipeResponseDTO() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();

        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        // When
        RecipeResponseDTO result = recipeService.getRecipeById("123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("123");
        assertThat(result.getTitle()).isEqualTo("Pizza");
        assertThat(result.getCategories()).containsExactly(RecipeCategory.CENA);
        assertThat(result.getIngredients()).containsExactly("Harina", "Agua");
        assertThat(result.getInstructions()).isEqualTo("Hornear 20 min");
        assertThat(result.getFit()).isTrue();
        assertThat(result.getImageUrl()).isEqualTo("http://test.com/cheesepizza.jpg");

        verify(recipeRepository).findById("123");

    }

    @Test
    void givenNonExisgtingRecipeId_whenGetRecipeById_thenThrowException() {
        // Given
        when(recipeRepository.findById("999")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> recipeService.getRecipeById("999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("La receta especificada no existe.");

        verify(recipeRepository).findById("999");
    }

    @Test
    void givenValidDtoAndImage_whenUpdateRecipe_thenRecipeIsUpdatedWithImageUrl() throws Exception {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();

        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        RecipeUpdateDTO dto = TestDataFactory.createRecipeUpdateDTO();

        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(imageService.uploadImage(image)).thenReturn("http://img.com/pizza.png");

        // When
        recipe = recipeService.updateRecipe(dto, image);

        // Then
        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService).uploadImage(image);
        verify(imageService).deleteImage("http://test.com/cheesepizza.jpg");
        assertThat(recipe.getTitle()).isEqualTo("Pizza");
        assertThat(recipe.getCategories()).containsExactly(RecipeCategory.CENA);
        assertThat(recipe.getIngredients()).containsExactly("harina", "agua", "queso");
        assertThat(recipe.getInstructions()).isEqualTo("Mezclar y hornear");
        assertThat(recipe.getFit()).isFalse();
        assertThat(recipe.getImageUrl()).isEqualTo("http://img.com/pizza.png");
    }

    @Test
    void givenValidDtoAndWithoutUpdatingImage_whenUpdateRecipe_thenRecipeIsUpdatedWithoutUpdatingImage() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();

        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        RecipeUpdateDTO dto = TestDataFactory.createRecipeUpdateDTO();

        // When
        recipe = recipeService.updateRecipe(dto, null);

        // Then
        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService, never()).uploadImage(any());
        verify(imageService, never()).deleteImage(any());
        assertThat(recipe.getTitle()).isEqualTo("Pizza");
        assertThat(recipe.getCategories()).containsExactly(RecipeCategory.CENA);
        assertThat(recipe.getIngredients()).containsExactly("harina", "agua", "queso");
        assertThat(recipe.getInstructions()).isEqualTo("Mezclar y hornear");
        assertThat(recipe.getFit()).isFalse();
        assertThat(recipe.getImageUrl()).isEqualTo("http://test.com/cheesepizza.jpg");
    }

    @Test
    void givenValidDtoWithoutExistingImageAndWithoutNewImage_whenUpdateRecipe_thenRecipeIsUpdatedWithoutAnImage() {
        // Given
        Recipe recipe = TestDataFactory.createRecipeWithoutImage();

        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        RecipeUpdateDTO dto = TestDataFactory.createRecipeUpdateDTO();

        // When
        recipe = recipeService.updateRecipe(dto, null);

        // Then
        verify(recipeRepository).save(any(Recipe.class));
        verify(imageService, never()).uploadImage(any());
        verify(imageService, never()).deleteImage(any());
        assertThat(recipe.getTitle()).isEqualTo("Pizza");
        assertThat(recipe.getCategories()).containsExactly(RecipeCategory.CENA);
        assertThat(recipe.getIngredients()).containsExactly("harina", "agua", "queso");
        assertThat(recipe.getInstructions()).isEqualTo("Mezclar y hornear");
        assertThat(recipe.getFit()).isFalse();
        assertThat(recipe.getImageUrl()).isNull();
    }

    @Test
    void givenNonExisgtingRecipeId_whenUpdateRecipe_thenThrowException() {
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setId("999");
        when(recipeRepository.findById("999")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> recipeService.updateRecipe(dto, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("La receta especificada no existe.");

        verify(recipeRepository).findById("999");
    }

    @Test
    void givenErrorWhenUpdatingRecipe_thenThrowsException() throws Exception {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();

        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        RecipeUpdateDTO dto = TestDataFactory.createRecipeUpdateDTO();

        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(imageService.uploadImage(image)).thenReturn("http://img.com/pizza.png");

        doThrow(new RuntimeException("DB error")).when(recipeRepository).save(any(Recipe.class));

        // When + then
        assertThatThrownBy(() -> recipeService.updateRecipe(dto, image))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error actualizando la receta");
        verify(imageService).deleteImage("http://test.com/cheesepizza.jpg");
    }

    @Test
    void givenNonExisgtingRecipeId_whenDeleteRecipe_thenThrowException() {
        // Given
        when(recipeRepository.findById("999")).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> recipeService.deleteRecipe("999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("La receta especificada no existe.");

        verify(recipeRepository).findById("999");
    }

    @Test
    void givenValidRecipeIdWithoutImage_whenDeleteRecipe_thenRecipeIsDeleted() {
        // Given
        Recipe recipe = TestDataFactory.createRecipeWithoutImage();

        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        // When
        recipeService.deleteRecipe(recipe.getId());

        // Then
        verify(recipeRepository).delete(any(Recipe.class));
        verify(imageService, never()).deleteImage(any());
    }

    @Test
    void givenValidRecipeIdWithImage_whenDeleteRecipe_thenRecipeAndImageAreDeleted() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();

        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        // When
        recipeService.deleteRecipe(recipe.getId());

        // Then
        verify(recipeRepository).delete(any(Recipe.class));
        verify(imageService).deleteImage("http://test.com/cheesepizza.jpg");
    }

    @Test
    void givenValidRecipeIdWithImage_whenImageDeletionFails_thenRecipeIsDeletedAnyway() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();
        when(recipeRepository.findById("123")).thenReturn(Optional.of(recipe));

        doThrow(new ImageDeletionException("Error al eliminar la imagen"))
                .when(imageService).deleteImage("http://test.com/cheesepizza.jpg");

        // When
        recipeService.deleteRecipe(recipe.getId());

        // Then
        verify(imageService).deleteImage("http://test.com/cheesepizza.jpg");
        verify(recipeRepository).delete(any(Recipe.class));
    }

    @Test
    void givenNoFilters_whenGetAllRecipesWithFilter_thenReturnAllRecipesPaged() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class)))
                .thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class)))
                .thenReturn(1L);

        // When
        Page<RecipeFilteredResponseDTO> result = recipeService.getAllRecipesWithFilter(null, null, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        RecipeFilteredResponseDTO dto = result.getContent().get(0);
        assertThat(dto.getTitle()).isEqualTo("Pizza");
        assertThat(dto.getCategories()).containsExactly(RecipeCategory.CENA);
        assertThat(dto.getFit()).isTrue();

        verify(mongoTemplate).find(any(Query.class), eq(Recipe.class));
        verify(mongoTemplate).count(any(Query.class), eq(Recipe.class));
    }

    @Test
    void givenCategoryFilter_whenGetAllRecipesWithFilter_thenReturnRecipesPagedWithThatCategory() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);
        List<RecipeCategory> categories = List.of(RecipeCategory.CENA);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class)))
                .thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class)))
                .thenReturn(1L);

        // When
        Page<RecipeFilteredResponseDTO> result = recipeService.getAllRecipesWithFilter(categories, null, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        RecipeFilteredResponseDTO dto = result.getContent().get(0);
        assertThat(dto.getTitle()).isEqualTo("Pizza");
        assertThat(dto.getCategories()).containsExactly(RecipeCategory.CENA);
        assertThat(dto.getFit()).isTrue();

        verify(mongoTemplate).find(any(Query.class), eq(Recipe.class));
        verify(mongoTemplate).count(any(Query.class), eq(Recipe.class));
    }

    @Test
    void givenFitFilter_whenGetAllRecipesWithFilter_thenReturnRecipesPagedFilteredByFit() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class)))
                .thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class)))
                .thenReturn(1L);

        // When
        Page<RecipeFilteredResponseDTO> result = recipeService.getAllRecipesWithFilter(null, true, 0,
                10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        RecipeFilteredResponseDTO dto = result.getContent().get(0);
        assertThat(dto.getTitle()).isEqualTo("Pizza");
        assertThat(dto.getCategories()).containsExactly(RecipeCategory.CENA);
        assertThat(dto.getFit()).isTrue();

        verify(mongoTemplate).find(any(Query.class), eq(Recipe.class));
        verify(mongoTemplate).count(any(Query.class), eq(Recipe.class));
    }

    @Test
    void givenFitAndCategoryFilter_whenGetAllRecipesWithFilter_thenReturnRecipesPagedFilteredByFitAndCategory() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);
        List<RecipeCategory> categories = List.of(RecipeCategory.CENA);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class)))
                .thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class)))
                .thenReturn(1L);

        // When
        Page<RecipeFilteredResponseDTO> result = recipeService.getAllRecipesWithFilter(categories, true, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        RecipeFilteredResponseDTO dto = result.getContent().get(0);
        assertThat(dto.getTitle()).isEqualTo("Pizza");
        assertThat(dto.getCategories()).containsExactly(RecipeCategory.CENA);
        assertThat(dto.getFit()).isTrue();

        verify(mongoTemplate).find(any(Query.class), eq(Recipe.class));
        verify(mongoTemplate).count(any(Query.class), eq(Recipe.class));
    }

    @Test
    void givenNullIngredients_whenGetAllRecipesWithIngredients_thenReturnAllRecipesPaged() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(1L);

        // When
        Page<RecipeFilteredResponseDTO> result = recipeService.getAllRecipesWithIngredients(null, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        RecipeFilteredResponseDTO dto = result.getContent().get(0);
        assertThat(dto.getTitle()).isEqualTo("Pizza");

        verify(mongoTemplate).find(any(Query.class), eq(Recipe.class));
        verify(mongoTemplate).count(any(Query.class), eq(Recipe.class));
    }

    @Test
    void givenEmptyIngredients_whenGetAllRecipesWithIngredients_thenReturnAllRecipesPaged() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(1L);

        // When
        Page<RecipeFilteredResponseDTO> result = recipeService.getAllRecipesWithIngredients(Collections.emptyList(), 0,
                10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(mongoTemplate).find(any(Query.class), eq(Recipe.class));
        verify(mongoTemplate).count(any(Query.class), eq(Recipe.class));
    }

    @Test
    void givenIngredientsList_whenGetAllRecipesWithIngredients_thenReturnRecipesPagedFilteredByIngredients() {
        // Given
        Recipe recipe = TestDataFactory.createRecipe();
        List<Recipe> recipeList = List.of(recipe);

        List<String> ingredients = List.of("Harina", "Agua");

        when(mongoTemplate.find(any(Query.class), eq(Recipe.class))).thenReturn(recipeList);
        when(mongoTemplate.count(any(Query.class), eq(Recipe.class))).thenReturn(1L);

        // When
        Page<RecipeFilteredResponseDTO> result = recipeService.getAllRecipesWithIngredients(ingredients, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        RecipeFilteredResponseDTO dto = result.getContent().get(0);
        assertThat(dto.getTitle()).isEqualTo("Pizza");

        verify(mongoTemplate).find(any(Query.class), eq(Recipe.class));
        verify(mongoTemplate).count(any(Query.class), eq(Recipe.class));
    }
}
