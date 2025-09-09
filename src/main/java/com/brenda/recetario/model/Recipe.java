package com.brenda.recetario.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "recipes")
public class Recipe {
    @Id
    private String id; // Mongo use ObjectId, but it is mapped as a String
    private String title;
    private String category;
    private List<String> ingredients;
    private String instructions;
    private boolean isFit;
    private String urlImage;
}
