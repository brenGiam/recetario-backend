package com.brenda.recetario.models;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;
import com.brenda.recetario.utils.RecipeTestDataFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipeResponseDTOTest {

    @Test
    void whenConstructedWithValidRecipe_thenFieldsAreMappedCorrectly() {
        // Given
        Recipe recipe = RecipeTestDataFactory.createValidRecipeEntity();

        // When
        RecipeResponseDTO dto = new RecipeResponseDTO(recipe);

        // Then
        assertThat(dto.getId()).isEqualTo(recipe.getId());
        assertThat(dto.getTitle()).isEqualTo(recipe.getTitle());
        assertThat(dto.getCategories()).containsExactlyElementsOf(recipe.getCategories());
        assertThat(dto.getIngredients()).containsExactlyElementsOf(recipe.getIngredients());
        assertThat(dto.getInstructions()).isEqualTo(recipe.getInstructions());
        assertThat(dto.getFit()).isEqualTo(recipe.getFit());
        assertThat(dto.getImageUrl()).isEqualTo(recipe.getImageUrl());
    }

    @Test
    void whenUsingNoArgsConstructor_thenFieldsAreNullOrEmpty() {
        // Given
        RecipeResponseDTO dto = new RecipeResponseDTO();

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getCategories()).isNull();
        assertThat(dto.getIngredients()).isNull();
        assertThat(dto.getInstructions()).isNull();
        assertThat(dto.getFit()).isNull();
        assertThat(dto.getImageUrl()).isNull();
    }

    @Test
    void whenUsingSetters_thenValuesAreStoredCorrectly() {
        // Given
        RecipeResponseDTO dto = new RecipeResponseDTO();
        dto.setId("456");
        dto.setTitle("Ensalada César");
        dto.setCategories(List.of(RecipeCategory.ALMUERZO));
        dto.setIngredients(List.of("Lechuga", "Pollo", "Croutons"));
        dto.setInstructions("Mezclar y servir frío");
        dto.setFit(false);
        dto.setImageUrl("https://example.com/ensalada.jpg");

        // Then
        assertThat(dto.getId()).isEqualTo("456");
        assertThat(dto.getTitle()).isEqualTo("Ensalada César");
        assertThat(dto.getCategories()).containsExactly(RecipeCategory.ALMUERZO);
        assertThat(dto.getIngredients()).containsExactly("Lechuga", "Pollo", "Croutons");
        assertThat(dto.getInstructions()).isEqualTo("Mezclar y servir frío");
        assertThat(dto.getFit()).isFalse();
        assertThat(dto.getImageUrl()).isEqualTo("https://example.com/ensalada.jpg");
    }

    @Test
    void whenConstructedWithNullRecipe_thenThrowsNullPointerException() {
        // Expect
        assertThatThrownBy(() -> new RecipeResponseDTO(null))
                .isInstanceOf(NullPointerException.class);
    }
}
