package com.spring.recipegradle.repositories.reactive;

import com.spring.recipegradle.domain.Category;
import com.spring.recipegradle.domain.Recipe;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RecipeReactiveRepository extends ReactiveMongoRepository<Recipe, String> {
}
