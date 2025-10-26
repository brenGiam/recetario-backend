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

public class RecipeCreateDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenTitleIsBlank_thenValidationFails() {
        RecipeCreateDTO dto = RecipeTestDataFactory.createRecipeCreateDTOWithTitle("");

        Set<ConstraintViolation<RecipeCreateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("title")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotBlank);
    }

    @Test
    void whenIngredientsIsEmpty_thenValidationFails() {
        RecipeCreateDTO dto = RecipeTestDataFactory.createRecipeCreateDTOWithIngredients(List.of());

        Set<ConstraintViolation<RecipeCreateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("ingredients")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotEmpty);
    }

    @Test
    void whenInstructionsIsBlank_thenValidationFails() {
        RecipeCreateDTO dto = RecipeTestDataFactory.createRecipeCreateDTOWithInstructions("");

        Set<ConstraintViolation<RecipeCreateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("instructions")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotBlank);
    }

    @Test
    void whenCategoryIsNull_thenValidationFails() {
        RecipeCreateDTO dto = RecipeTestDataFactory.createRecipeCreateDTOWithCategories(null);

        Set<ConstraintViolation<RecipeCreateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("categories")
                        && v.getMessage().equals("Debe seleccionar al menos una categoría"));
    }

    @Test
    void whenCategoryIsEmpty_thenValidationFails() {
        RecipeCreateDTO dto = RecipeTestDataFactory.createRecipeCreateDTOWithCategories(List.of());

        Set<ConstraintViolation<RecipeCreateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("categories")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotEmpty);
    }

    @Test
    void whenFitIsNull_thenValidationFails() {
        RecipeCreateDTO dto = RecipeTestDataFactory.createRecipeCreateDTOWithFit(null);

        Set<ConstraintViolation<RecipeCreateDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("fit")
                        && v.getConstraintDescriptor()
                                .getAnnotation() instanceof jakarta.validation.constraints.NotNull);
    }

    @Test
    void whenDtoIsValid_thenNoViolations() {
        RecipeCreateDTO dto = RecipeTestDataFactory.createValidRecipeCreateDTO();

        Set<ConstraintViolation<RecipeCreateDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
