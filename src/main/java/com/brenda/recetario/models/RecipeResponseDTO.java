package com.brenda.recetario.models;

import java.util.List;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecipeResponseDTO {
    private String id;
    @NotBlank
    private String title;
    @NotNull
    private RecipeCategory category;
    @NotEmpty
    private List<String> ingredients;
    @NotBlank
    private String instructions;
    private Boolean fit;
    private String imageUrl;

    public RecipeResponseDTO(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.category = recipe.getCategory();
        this.ingredients = recipe.getIngredients();
        this.instructions = recipe.getInstructions();
        this.fit = recipe.getFit();
        this.imageUrl = recipe.getImageUrl();
    }
}
