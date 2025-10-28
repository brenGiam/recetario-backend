package com.brenda.recetario.models;

import java.util.List;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RecipeResponseDTO {
    private String id;
    private String title;
    private List<RecipeCategory> categories;
    private List<String> ingredients;
    private String instructions;
    private Boolean fit;
    private String imageUrl;

    public RecipeResponseDTO(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.categories = recipe.getCategories();
        this.ingredients = recipe.getIngredients();
        this.instructions = recipe.getInstructions();
        this.fit = recipe.getFit();
        this.imageUrl = recipe.getImageUrl();
    }
}
