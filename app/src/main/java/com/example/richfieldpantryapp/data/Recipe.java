package com.example.richfieldpantryapp.data;

//A recipe in the preloaded collection


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;


@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name = "";

    @NonNull
    public String description = "";

    @NonNull
    public  String steps = "";

    public Recipe() {
    }

    @Ignore
    public  Recipe(@NonNull String name, @NonNull String description, @NonNull String steps) {
        this.name = name;
        this.description = description;
        this.steps = steps;
    }

    //TODO: Add steplist to split steps into new lines
    public List<String> stepList() {
        List<String> list = new ArrayList<>();
        for (String line : steps.split("\n")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                list.add(trimmed);
            }
        }
        return list;
    }

}
