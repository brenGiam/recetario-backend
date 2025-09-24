package com.brenda.recetario.models;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class RecipeResponseDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenTitleIsBlank_thenValidationFails() {
        RecipeResponseDTO dto = new RecipeResponseDTO();
        dto.setId("1");
        dto.setTitle(""); // invalid
        dto.setCategory(RecipeCategory.CENA);
        dto.setIngredients(List.of("Harina"));
        dto.setInstructions("Mezclar todo");
        dto.setFit(true);
        dto.setImageUrl("http://test.com/img.png");

        Set<ConstraintViolation<RecipeResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("no debe estar vacío");
    }

    @Test
    void whenDtoIsValid_thenNoViolations() {
        RecipeResponseDTO dto = new RecipeResponseDTO();
        dto.setId("1");
        dto.setTitle("Pizza");
        dto.setCategory(RecipeCategory.CENA);
        dto.setIngredients(List.of("Harina", "Queso"));
        dto.setInstructions("Hornear 20 minutos");
        dto.setFit(true);
        dto.setImageUrl("http://test.com/img.png");

        Set<ConstraintViolation<RecipeResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void constructorShouldMapFieldsCorrectly() {
        // given
        Recipe recipe = new Recipe();
        recipe.setId("123");
        recipe.setTitle("Pizza");
        recipe.setCategory(RecipeCategory.CENA);
        recipe.setIngredients(List.of("Harina", "Tomate", "Queso"));
        recipe.setInstructions("Hornear 20 min");
        recipe.setFit(true);
        recipe.setImageUrl("http://image.com/pizza.jpg");

        // when
        RecipeResponseDTO dto = new RecipeResponseDTO(recipe);

        // then
        assertThat(dto.getId()).isEqualTo("123");
        assertThat(dto.getTitle()).isEqualTo("Pizza");
        assertThat(dto.getCategory()).isEqualTo(RecipeCategory.CENA);
        assertThat(dto.getIngredients()).containsExactly("Harina", "Tomate", "Queso");
        assertThat(dto.getInstructions()).isEqualTo("Hornear 20 min");
        assertThat(dto.getFit()).isTrue();
        assertThat(dto.getImageUrl()).isEqualTo("http://image.com/pizza.jpg");
    }
}
