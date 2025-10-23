package com.brenda.recetario.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.brenda.recetario.enums.RecipeCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Document(collection = "recipes")
public class Recipe {
    @Id
    private String id; // Mongo uses ObjectId, but it is mapped as a String
    @NotBlank
    private String title;
    @NotEmpty
    private List<RecipeCategory> categories = new ArrayList<>();
    @NotEmpty
    private List<String> ingredients;
    @NotBlank
    private String instructions;
    @NotNull
    private Boolean fit;
    private String imageUrl;
    private String normalizedTitle;
    private List<String> normalizedIngredients;
}
