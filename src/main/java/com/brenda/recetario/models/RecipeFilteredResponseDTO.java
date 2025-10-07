package com.brenda.recetario.models;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RecipeFilteredResponseDTO {
    private String id;
    private String title;
    private RecipeCategory category;
    private Boolean fit;
    private String imageUrl;

    public RecipeFilteredResponseDTO(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.category = recipe.getCategory();
        this.fit = recipe.getFit();
        this.imageUrl = recipe.getImageUrl();
    }
}
