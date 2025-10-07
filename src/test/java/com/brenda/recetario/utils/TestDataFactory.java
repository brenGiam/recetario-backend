package com.brenda.recetario.utils;

import java.util.List;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;
import com.brenda.recetario.models.RecipeCreateDTO;
import com.brenda.recetario.models.RecipeUpdateDTO;

public class TestDataFactory {
    public static Recipe createRecipe() {
        Recipe recipe = new Recipe();
        recipe.setId("123");
        recipe.setTitle("Pizza");
        recipe.setCategory(RecipeCategory.CENA);
        recipe.setIngredients(List.of("Harina", "Agua"));
        recipe.setInstructions("Hornear 20 min");
        recipe.setFit(true);
        recipe.setImageUrl("http://test.com/cheesepizza.jpg");
        return recipe;
    }

    public static Recipe createRecipeWithoutImage() {
        Recipe recipe = new Recipe();
        recipe.setId("123");
        recipe.setTitle("Pizza");
        recipe.setCategory(RecipeCategory.CENA);
        recipe.setIngredients(List.of("Harina", "Agua"));
        recipe.setInstructions("Hornear 20 min");
        recipe.setFit(true);
        return recipe;
    }

    public static RecipeCreateDTO createRecipeCreateDTO() {
        RecipeCreateDTO dto = new RecipeCreateDTO();
        dto.setTitle("Pizza");
        dto.setCategory(RecipeCategory.CENA);
        dto.setIngredients(List.of("Harina", "Agua"));
        dto.setInstructions("Mezclar y hornear");
        dto.setFit(true);
        return dto;
    }

    public static RecipeUpdateDTO createRecipeUpdateDTO() {
        RecipeUpdateDTO dto = new RecipeUpdateDTO();
        dto.setId("123");
        dto.setTitle("Pizza");
        dto.setCategory(RecipeCategory.CENA);
        dto.setIngredients(List.of("Harina", "Agua", "Queso"));
        dto.setInstructions("Mezclar y hornear");
        dto.setFit(false);
        return dto;
    }
}
