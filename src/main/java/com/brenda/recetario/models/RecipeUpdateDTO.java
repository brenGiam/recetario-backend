package com.brenda.recetario.models;

import java.util.List;

import com.brenda.recetario.enums.RecipeCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecipeUpdateDTO {
    private String id;
    @NotBlank
    private String title;
    @NotEmpty
    private List<RecipeCategory> categories;
    @NotEmpty
    private List<String> ingredients;
    @NotBlank
    private String instructions;
    @NotNull
    private Boolean fit;
    private String imageUrl;
}
