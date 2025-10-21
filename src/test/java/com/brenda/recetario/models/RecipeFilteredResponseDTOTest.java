package com.brenda.recetario.models;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;

import static org.assertj.core.api.Assertions.assertThat;

public class RecipeFilteredResponseDTOTest {

    @Test
    void constructorShouldMapFieldsCorrectly() {
        // Given: a valid recipe
        Recipe recipe = new Recipe();
        recipe.setId("123");
        recipe.setTitle("Pizza");
        recipe.setCategories(List.of(RecipeCategory.CENA));
        recipe.setIngredients(List.of("Harina", "Tomate", "Queso"));
        recipe.setInstructions("Hornear 20 min");
        recipe.setFit(true);
        recipe.setImageUrl("http://image.com/pizza.jpg");

        // When: DTO is created
        RecipeFilteredResponseDTO dto = new RecipeFilteredResponseDTO(recipe);

        // Then: response should be correct
        assertThat(dto.getId()).isEqualTo("123");
        assertThat(dto.getTitle()).isEqualTo("Pizza");
        assertThat(dto.getCategories()).containsExactly(RecipeCategory.CENA);
        assertThat(dto.getFit()).isTrue();
        assertThat(dto.getImageUrl()).isEqualTo("http://image.com/pizza.jpg");
    }
}
