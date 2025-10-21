package com.brenda.recetario.models;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.brenda.recetario.enums.RecipeCategory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

//No test for default constructors because of Lombok
public class RecipeUpdateDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenTitleIsBlank_thenValidationFails() {

        // Given: an empty title DTO
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setId("123");
        dto.setTitle(""); // invalid
        dto.setCategories(List.of(RecipeCategory.CENA));
        dto.setIngredients(List.of("Harina"));
        dto.setInstructions("Mezclar todo");
        dto.setFit(true);

        // When: DTO is validated
        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        // Then: validation should fail because of the empty title
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("title") &&
                        v.getConstraintDescriptor().getAnnotation() instanceof jakarta.validation.constraints.NotBlank);
    }

    @Test
    void whenIngredientsIsEmpty_thenValidationFails() {

        // Given: an empty ingredients DTO
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setId("123");
        dto.setTitle("Pizza");
        dto.setCategories(List.of(RecipeCategory.CENA));
        dto.setIngredients(List.of()); // empty
        dto.setInstructions("Mezclar todo");
        dto.setFit(true);

        // When: DTO is validated
        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        // Then: validation should fail because of the empty ingredients
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("ingredients") &&
                        v.getConstraintDescriptor().getAnnotation() instanceof jakarta.validation.constraints.NotEmpty);
    }

    @Test
    void whenInstructionsIsBlank_thenValidationFails() {

        // Given: a blank instructions DTO
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setId("123");
        dto.setTitle("Pizza");
        dto.setCategories(List.of(RecipeCategory.CENA));
        dto.setIngredients(List.of("Harina"));
        dto.setInstructions(""); // empty
        dto.setFit(true);

        // When: DTO is validated
        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        // Then: validation should fail because of the blank instructions
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("instructions") &&
                        v.getConstraintDescriptor().getAnnotation() instanceof jakarta.validation.constraints.NotBlank);
    }

    @Test
    void whenCategoriesIsNull_thenValidationFails() {

        // Given: a null categories DTO
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setId("123");
        dto.setTitle("Pizza");
        dto.setCategories(null); // invalid
        dto.setIngredients(List.of("Harina"));
        dto.setInstructions("Hornear");
        dto.setFit(true);

        // When: DTO is validated
        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        // Then: validation should fail because the list of categories is null or empty
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("categories") &&
                        v.getMessage().contains("no debe estar vacío"));
    }

    @Test
    void whenFitIsNull_thenValidationFails() {

        // Given: a null isFit DTO
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setId("123");
        dto.setTitle("Pizza");
        dto.setCategories(List.of(RecipeCategory.CENA));
        dto.setIngredients(List.of("Harina"));
        dto.setInstructions("Hornear");
        dto.setFit(null); // invalid

        // When: DTO is validated
        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        // Then: validation should fail because of the null isFit
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("fit") &&
                        v.getConstraintDescriptor().getAnnotation() instanceof jakarta.validation.constraints.NotNull);
    }

    @Test
    void whenDtoIsValid_thenNoViolations() {

        // Given: a valid DTO
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setTitle("Pizza");
        dto.setCategories(List.of(RecipeCategory.CENA));
        dto.setIngredients(List.of("Harina", "Queso"));
        dto.setInstructions("Hornear 20 minutos");
        dto.setFit(true);

        // When: DTO is validated
        Set<ConstraintViolation<RecipeUpdateDTO>> violations = validator.validate(dto);

        // Then: validation shouldn't fail
        assertThat(violations).isEmpty();
    }
}
