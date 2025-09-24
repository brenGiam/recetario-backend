package com.brenda.recetario.models;

import com.brenda.recetario.entity.Recipe;
import com.brenda.recetario.enums.RecipeCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RecipeFilteredResponseDTO {
    private String id;
    @NotBlank
    private String title;
    @NotNull
    private RecipeCategory category;
    @NotNull
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
