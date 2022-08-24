package com.spring.recipegradle.services;

import com.spring.recipegradle.commands.RecipeCommand;
import com.spring.recipegradle.converters.RecipeCommandToRecipe;
import com.spring.recipegradle.converters.RecipeToRecipeCommand;
import com.spring.recipegradle.domain.Recipe;
import com.spring.recipegradle.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
    private RecipeReactiveRepository recipeRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("in recipe service!");
        Set<Recipe> recipes = new HashSet<>();
//        recipeRepository.findAll().iterator().forEachRemaining(recipes::add);
        return recipeRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String l) {
        return recipeRepository.findById(l);
    }

    @Override
    public Mono<RecipeCommand> findCommandById(String l) {
        return recipeRepository.findById(l)
                .map(recipe -> {
                    RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);
                    recipeCommand.getIngredients().forEach(rc -> {
                        rc.setRecipeId(recipeCommand.getId());
                    });
                    return recipeCommand;
                });
    }

    @Override
    public RecipeCommand saveRecipeCommand(RecipeCommand command) {

        return recipeRepository.save(recipeCommandToRecipe.convert(command)).map(recipeToRecipeCommand::convert).block();
    }

    @Override
    public void deleteById(String id) {
        recipeRepository.deleteById(id).block();
    }
}
