package com.spring.recipegradle.repositories;

import com.spring.recipegradle.domain.Category;
import com.spring.recipegradle.domain.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, String> {
    Optional<Recipe> findByDescription(String description);
}
