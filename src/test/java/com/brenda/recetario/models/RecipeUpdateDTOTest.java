package com.brenda.recetario.models;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brenda.recetario.utils.RecipeTestDataFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeUpdateDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenTitleIsBlank_thenValidationFails() {
        RecipeUpdateDTO dto = RecipeTestDataFactory.createValidRecipeUpdateDTO();
        dto.setTitle("");

        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotBlank);
    }

    @Test
    void whenCategoriesIsEmpty_thenValidationFails() {
        RecipeUpdateDTO dto = RecipeTestDataFactory.createValidRecipeUpdateDTO();
        dto.setCategories(List.of());

        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("categories")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotEmpty);
    }

    @Test
    void whenIngredientsIsEmpty_thenValidationFails() {
        RecipeUpdateDTO dto = RecipeTestDataFactory.createValidRecipeUpdateDTO();
        dto.setIngredients(List.of());

        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("ingredients")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotEmpty);
    }

    @Test
    void whenInstructionsIsBlank_thenValidationFails() {
        RecipeUpdateDTO dto = RecipeTestDataFactory.createValidRecipeUpdateDTO();
        dto.setInstructions("");

        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("instructions")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotBlank);
    }

    @Test
    void whenFitIsNull_thenValidationFails() {
        RecipeUpdateDTO dto = RecipeTestDataFactory.createValidRecipeUpdateDTO();
        dto.setFit(null);

        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("fit")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotNull);
    }

    @Test
    void whenIdIsMissing_thenValidationStillPasses() {
        RecipeUpdateDTO dto = RecipeTestDataFactory.createValidRecipeUpdateDTO();
        dto.setId(null);

        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty(); // id is optional
    }

    @Test
    void whenImageUrlIsMissing_thenValidationStillPasses() {
        RecipeUpdateDTO dto = RecipeTestDataFactory.createValidRecipeUpdateDTO();
        dto.setImageUrl(null);

        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty(); // imageUrl is optional
    }

    @Test
    void whenDtoIsValid_thenNoViolations() {
        RecipeUpdateDTO dto = RecipeTestDataFactory.createValidRecipeUpdateDTO();

        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
