package com.sanj.teapot.models;

public class Tea {
     private String description,name,type,origin,ingredients,caffeineLevel;
     private boolean favorite;
     private int id;

    public Tea() {
    }

    public Tea(int id,String description, String name, String type, String origin, String ingredients, String caffeineLevel, boolean favorite) {
        this.description = description;
        this.name = name;
        this.type = type;
        this.origin = origin;
        this.ingredients = ingredients;
        this.caffeineLevel = caffeineLevel;
        this.favorite = favorite;
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getOrigin() {
        return origin;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getCaffeineLevel() {
        return caffeineLevel;
    }
}
