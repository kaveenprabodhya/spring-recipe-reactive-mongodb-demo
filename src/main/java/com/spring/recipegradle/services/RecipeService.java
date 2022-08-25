package com.spring.recipegradle.services;

import com.spring.recipegradle.commands.RecipeCommand;
import com.spring.recipegradle.domain.Recipe;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecipeService {
    Flux<Recipe> getRecipes();
    Mono<Recipe> findById(String l);
    Mono<RecipeCommand> findCommandById(String l);
    Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command);
    Mono<ResponseEntity<Void>> deleteById(String id);
}
