package com.brenda.recetario.utils;

import java.util.List;
import java.util.UUID;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;
import com.brenda.recetario.models.RecipeCreateDTO;
import com.brenda.recetario.models.RecipeUpdateDTO;

public class RecipeTestDataFactory {

    private RecipeTestDataFactory() {
    }

    public static RecipeCreateDTO createValidRecipeCreateDTO() {
        RecipeCreateDTO dto = new RecipeCreateDTO();
        dto.setTitle("Pizza");
        dto.setCategories(List.of(RecipeCategory.CENA));
        dto.setIngredients(List.of("Harina", "Queso", "Tomate"));
        dto.setInstructions("Hornear 20 minutos a 200°C");
        dto.setFit(true);
        return dto;
    }

    public static RecipeCreateDTO createRecipeCreateDTOWithTitle(String title) {
        RecipeCreateDTO dto = createValidRecipeCreateDTO();
        dto.setTitle(title);
        return dto;
    }

    public static RecipeCreateDTO createRecipeCreateDTOWithCategories(List<RecipeCategory> categories) {
        RecipeCreateDTO dto = createValidRecipeCreateDTO();
        dto.setCategories(categories);
        return dto;
    }

    public static RecipeCreateDTO createRecipeCreateDTOWithIngredients(List<String> ingredients) {
        RecipeCreateDTO dto = createValidRecipeCreateDTO();
        dto.setIngredients(ingredients);
        return dto;
    }

    public static RecipeCreateDTO createRecipeCreateDTOWithInstructions(String instructions) {
        RecipeCreateDTO dto = createValidRecipeCreateDTO();
        dto.setInstructions(instructions);
        return dto;
    }

    public static RecipeCreateDTO createRecipeCreateDTOWithFit(Boolean fit) {
        RecipeCreateDTO dto = createValidRecipeCreateDTO();
        dto.setFit(fit);
        return dto;
    }

    public static Recipe createValidRecipeEntity() {
        Recipe recipe = new Recipe();
        recipe.setId(UUID.randomUUID().toString());
        recipe.setTitle("Pizza Margarita");
        recipe.setCategories(List.of(RecipeCategory.CENA));
        recipe.setFit(true);
        recipe.setImageUrl("https://example.com/pizza.jpg");
        recipe.setIngredients(List.of("Harina", "Queso", "Tomate"));
        recipe.setInstructions("Hornear 20 minutos a 200°C");
        return recipe;
    }

    public static RecipeUpdateDTO createValidRecipeUpdateDTO() {
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setId(UUID.randomUUID().toString());
        dto.setTitle("Pizza Margarita");
        dto.setCategories(List.of(RecipeCategory.CENA));
        dto.setIngredients(List.of("Harina", "Queso", "Tomate"));
        dto.setInstructions("Hornear 20 minutos a 200°C");
        dto.setFit(true);
        dto.setImageUrl("https://example.com/pizza.jpg");
        return dto;
    }
}
