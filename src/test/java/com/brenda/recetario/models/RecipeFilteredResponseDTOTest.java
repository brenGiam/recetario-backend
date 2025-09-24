package com.brenda.recetario.models;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

public class RecipeFilteredResponseDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenTitleIsBlank_thenValidationFails() {

        // Given: an empty title DTO
        RecipeFilteredResponseDTO dto = new RecipeFilteredResponseDTO();
        dto.setTitle(""); // invalid
        dto.setCategory(RecipeCategory.CENA);
        dto.setFit(true);

        // When: DTO is validated
        Set<ConstraintViolation<RecipeFilteredResponseDTO>> violations = validator.validate(dto);

        // Then: validation should fail because of the empty title
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("title") &&
                        v.getConstraintDescriptor().getAnnotation() instanceof jakarta.validation.constraints.NotBlank);
    }

    @Test
    void whenCategoryIsNull_thenValidationFails() {

        // Given: a null category DTO
        RecipeFilteredResponseDTO dto = new RecipeFilteredResponseDTO();
        dto.setTitle("Pizza");
        dto.setCategory(null); // invalid
        dto.setFit(true);

        // When: DTO is validated
        Set<ConstraintViolation<RecipeFilteredResponseDTO>> violations = validator.validate(dto);

        // Then: validation should fail because of the null category
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("category") &&
                        v.getConstraintDescriptor().getAnnotation() instanceof jakarta.validation.constraints.NotNull);
    }

    @Test
    void whenFitIsNull_thenValidationFails() {

        // Given: a null isFit DTO
        RecipeFilteredResponseDTO dto = new RecipeFilteredResponseDTO();
        dto.setTitle("Pizza");
        dto.setCategory(RecipeCategory.CENA);
        dto.setFit(null); // invalid

        // When: DTO is validated
        Set<ConstraintViolation<RecipeFilteredResponseDTO>> violations = validator.validate(dto);

        // Then: validation should fail because of the null isFit
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("fit") &&
                        v.getConstraintDescriptor().getAnnotation() instanceof jakarta.validation.constraints.NotNull);
    }

    @Test
    void constructorShouldMapFieldsCorrectly() {
        // Given: a valid recipe
        Recipe recipe = new Recipe();
        recipe.setId("123");
        recipe.setTitle("Pizza");
        recipe.setCategory(RecipeCategory.CENA);
        recipe.setIngredients(List.of("Harina", "Tomate", "Queso"));
        recipe.setInstructions("Hornear 20 min");
        recipe.setFit(true);
        recipe.setImageUrl("http://image.com/pizza.jpg");

        // When: DTO is created
        RecipeFilteredResponseDTO dto = new RecipeFilteredResponseDTO(recipe);

        // Then: response should be correct
        assertThat(dto.getId()).isEqualTo("123");
        assertThat(dto.getTitle()).isEqualTo("Pizza");
        assertThat(dto.getCategory()).isEqualTo(RecipeCategory.CENA);
        assertThat(dto.getFit()).isTrue();
        assertThat(dto.getImageUrl()).isEqualTo("http://image.com/pizza.jpg");
    }
}
