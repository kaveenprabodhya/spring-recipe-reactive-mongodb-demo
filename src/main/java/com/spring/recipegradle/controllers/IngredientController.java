package com.spring.recipegradle.controllers;

import com.spring.recipegradle.commands.IngredientCommand;
import com.spring.recipegradle.commands.UnitOfMeasureCommand;
import com.spring.recipegradle.services.IngredientService;
import com.spring.recipegradle.services.RecipeService;
import com.spring.recipegradle.services.reactive.UnitOfMeasureReactiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class IngredientController {

    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    //    private final UnitOfMeasureService unitOfMeasureService;
    private final UnitOfMeasureReactiveService unitOfMeasureService;

    public IngredientController(IngredientService ingredientService,
                                RecipeService recipeService,
                                UnitOfMeasureReactiveService unitOfMeasureService) {
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model) {
        log.debug("Getting ingredient list for recipe id: " + recipeId);

        // use command object to avoid lazy load errors in Thymeleaf.
        model.addAttribute("recipe", recipeService.findCommandById(recipeId));

        return "recipe/ingredient/list";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String id, Model model) {
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public Mono<String> newRecipe(@PathVariable String recipeId, Model model) {

        return recipeService.findCommandById(recipeId)
                .flatMap(recipeCommand -> {
                    //need to return parent id for hidden form property
                    IngredientCommand ingredientCommand = new IngredientCommand();
                    ingredientCommand.setRecipeId(recipeId);
                    model.addAttribute("ingredient", ingredientCommand);

                    //init uom
                    ingredientCommand.setUom(new UnitOfMeasureCommand());

                    model.addAttribute("uomList", unitOfMeasureService.listAllUoms());
                    return Mono.just("recipe/ingredient/ingredientform");
                });
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model) {
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));

        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());
        return "recipe/ingredient/ingredientform";
    }

    @PostMapping("recipe/{recipeId}/ingredient")
    public Mono<String> saveOrUpdate(@ModelAttribute IngredientCommand command, @PathVariable String recipeId) {
        return ingredientService.saveIngredientCommand(command)
                .flatMap(ingredientCommand -> {
                    log.debug("saved ingredient id:" + ingredientCommand.getId());

                    return Mono.just(
                            "redirect:/recipe/" +
                                    recipeId +
                                    "/ingredient/" +
                                    ingredientCommand.getId() +
                                    "/show"
                    );
                });

    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
    public Mono<String> deleteIngredient(@PathVariable String recipeId,
                                         @PathVariable String id) {
        log.debug("deleting ingredient id:" + id);
        return ingredientService.deleteById(recipeId, id).flatMap(response -> {
            if (response.getStatusCode().is2xxSuccessful()) {
                return Mono.just("redirect:/recipe/" + recipeId + "/ingredients");
            }
            return Mono.just("redirect:/error404");
        });
    }
}
