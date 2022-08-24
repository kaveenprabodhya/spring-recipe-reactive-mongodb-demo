package com.spring.recipegradle.services;

import com.spring.recipegradle.commands.RecipeCommand;
import com.spring.recipegradle.domain.Recipe;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecipeService {
    Flux<Recipe> getRecipes();
    Mono<Recipe> findById(String l);
    Mono<RecipeCommand> findCommandById(String l);
    RecipeCommand saveRecipeCommand(RecipeCommand command);
    void deleteById(String id);
}
