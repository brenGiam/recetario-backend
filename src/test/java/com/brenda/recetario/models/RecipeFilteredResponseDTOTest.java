package com.brenda.recetario.models;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;
import com.brenda.recetario.utils.RecipeTestDataFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipeFilteredResponseDTOTest {

    @Test
    void whenConstructedWithValidRecipe_thenFieldsAreMappedCorrectly() {
        // Given
        Recipe recipe = RecipeTestDataFactory.createValidRecipeEntity();

        // When
        RecipeFilteredResponseDTO dto = new RecipeFilteredResponseDTO(recipe);

        // Then
        assertThat(dto.getId()).isEqualTo(recipe.getId());
        assertThat(dto.getTitle()).isEqualTo(recipe.getTitle());
        assertThat(dto.getCategories()).containsExactlyElementsOf(recipe.getCategories());
        assertThat(dto.getFit()).isEqualTo(recipe.getFit());
        assertThat(dto.getImageUrl()).isEqualTo(recipe.getImageUrl());
    }

    @Test
    void whenUsingNoArgsConstructor_thenFieldsAreNullOrEmpty() {
        // Given
        RecipeFilteredResponseDTO dto = new RecipeFilteredResponseDTO();

        // Then
        assertThat(dto.getId()).isNull();
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getCategories()).isNull();
        assertThat(dto.getFit()).isNull();
        assertThat(dto.getImageUrl()).isNull();
    }

    @Test
    void whenUsingSetters_thenValuesAreStoredCorrectly() {
        // Given
        RecipeFilteredResponseDTO dto = new RecipeFilteredResponseDTO();
        dto.setId("123");
        dto.setTitle("Tarta de Verduras");
        dto.setCategories(List.of(RecipeCategory.ALMUERZO));
        dto.setFit(false);
        dto.setImageUrl("https://example.com/tarta.jpg");

        // Then
        assertThat(dto.getId()).isEqualTo("123");
        assertThat(dto.getTitle()).isEqualTo("Tarta de Verduras");
        assertThat(dto.getCategories()).containsExactly(RecipeCategory.ALMUERZO);
        assertThat(dto.getFit()).isFalse();
        assertThat(dto.getImageUrl()).isEqualTo("https://example.com/tarta.jpg");
    }

    @Test
    void whenConstructedWithNullRecipe_thenThrowsNullPointerException() {
        // Expect
        assertThatThrownBy(() -> new RecipeFilteredResponseDTO(null))
                .isInstanceOf(NullPointerException.class);
    }
}
