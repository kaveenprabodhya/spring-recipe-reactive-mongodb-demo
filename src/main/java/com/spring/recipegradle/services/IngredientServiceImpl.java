package com.spring.recipegradle.services;

import com.spring.recipegradle.commands.IngredientCommand;
import com.spring.recipegradle.converters.IngredientCommandToIngredient;
import com.spring.recipegradle.converters.IngredientToIngredientCommand;
import com.spring.recipegradle.domain.Ingredient;
import com.spring.recipegradle.domain.Recipe;
import com.spring.recipegradle.repositories.reactive.RecipeReactiveRepository;
import com.spring.recipegradle.repositories.reactive.UnitOfMeasureReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeReactiveRepository recipeRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 RecipeReactiveRepository recipeRepository,
                                 UnitOfMeasureReactiveRepository unitOfMeasureRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {

        return recipeRepository
                .findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient -> {
                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
                    command.setRecipeId(recipeId);
                    return command;
                });

//        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
//
//        if (!recipeOptional.isPresent()){
//            log.error("recipe id not found. Id: " + recipeId);
//        }
//
//        Recipe recipe = recipeOptional.get();
//
//        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
//                .filter(ingredient -> ingredient.getId().equals(ingredientId))
//                .map( ingredient -> ingredientToIngredientCommand.convert(ingredient)).findFirst();
//
//        if(!ingredientCommandOptional.isPresent()){
//            log.error("Ingredient id not found: " + ingredientId);
//        }
//
//        return Mono.just(ingredientCommandOptional.get());
    }

    @Override
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
        return recipeRepository.findById(command.getRecipeId())
                .flatMap(recipe -> {
                    if (recipe == null) {
                        log.error("Recipe not found for id: " + command.getRecipeId());
                        return Mono.just(new IngredientCommand());
                    }

                    Optional<Ingredient> ingredientOptional = recipe
                            .getIngredients()
                            .stream()
                            .filter(ingredient -> ingredient.getId().equals(command.getId()))
                            .findFirst();

                    if (ingredientOptional.isPresent()) {
                        Ingredient ingredientFound = ingredientOptional.get();
                        ingredientFound.setDescription(command.getDescription());
                        ingredientFound.setAmount(command.getAmount());
                        unitOfMeasureRepository
                                .findById(command.getUom().getId()).flatMap(
                                        unitOfMeasure -> {
                                            ingredientFound.setUom(unitOfMeasure);
                                            return Mono.empty();
                                        }
                                );
//                        .orElseThrow(() -> new RuntimeException("UOM NOT FOUND")));
                        if (ingredientFound.getUom() == null) {
                            new RuntimeException("UOM NOT FOUND");
                        }
                    } else {
                        //add new Ingredient
                        Ingredient ingredient = ingredientCommandToIngredient.convert(command);
                        //  ingredient.setRecipe(recipe);
                        recipe.addIngredient(ingredient);
                    }

                    return recipeRepository.save(recipe)
                            .flatMap(savedRecipe -> {
                                Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                                        .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                                        .findFirst();

                                //check by description
                                if (!savedIngredientOptional.isPresent()) {
                                    //not totally safe... But best guess
                                    savedIngredientOptional = savedRecipe.getIngredients().stream()
                                            .filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
                                            .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
                                            .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(command.getUom().getId()))
                                            .findFirst();
                                }

                                //to do check for fail
                                IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand.convert(savedIngredientOptional.get());
                                ingredientCommandSaved.setRecipeId(recipe.getId());
                                return Mono.just(ingredientCommandSaved);
                            });
                });
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteById(String recipeId, String idToDelete) {

        log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

        return recipeRepository
                .findById(recipeId).flatMap(recipe -> {
                    log.debug("found recipe");

                    Optional<Ingredient> ingredientOptional = recipe
                            .getIngredients()
                            .stream()
                            .filter(ingredient -> ingredient.getId().equals(idToDelete))
                            .findFirst();

                    if (ingredientOptional.isPresent()) {
                        log.debug("found Ingredient");
                        Ingredient ingredientToDelete = ingredientOptional.get();
                        // ingredientToDelete.setRecipe(null);
                        recipe.getIngredients().remove(ingredientOptional.get());
                        return recipeRepository.save(recipe).flatMap(x -> Mono.just(ResponseEntity.ok().build()));
                    }
                    return Mono.just(ResponseEntity.notFound().build());
                });

    }
}
