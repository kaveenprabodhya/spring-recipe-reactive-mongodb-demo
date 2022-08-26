package com.spring.recipegradle.services;

import com.spring.recipegradle.commands.IngredientCommand;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IngredientService {
    Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId);
    Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command);
    Mono<ResponseEntity<Void>> deleteById(String recipeId, String idToDelete);
}
