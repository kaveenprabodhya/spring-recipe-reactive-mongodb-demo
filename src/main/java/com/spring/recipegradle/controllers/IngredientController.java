package com.spring.recipegradle.controllers;

import com.spring.recipegradle.commands.IngredientCommand;
import com.spring.recipegradle.commands.RecipeCommand;
import com.spring.recipegradle.commands.UnitOfMeasureCommand;
import com.spring.recipegradle.converters.RecipeCommandToRecipe;
import com.spring.recipegradle.services.IngredientService;
import com.spring.recipegradle.services.RecipeService;
import com.spring.recipegradle.services.UnitOfMeasureService;
import com.spring.recipegradle.services.reactive.UnitOfMeasureReactiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String listIngredients(@PathVariable String recipeId, Model model){
        log.debug("Getting ingredient list for recipe id: " + recipeId);

        // use command object to avoid lazy load errors in Thymeleaf.
        model.addAttribute("recipe", recipeService.findCommandById(recipeId).block());

        return "recipe/ingredient/list";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id).block());
        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public String newRecipe(@PathVariable String recipeId, Model model){

        //make sure we have a good id value
        RecipeCommand recipeCommand = recipeService.findCommandById(recipeId).block();
        if(recipeCommand.getId().isEmpty()){
            throw new RuntimeException("Recipe not found: "+recipeId);
        }

        //need to return parent id for hidden form property
        IngredientCommand ingredientCommand = new IngredientCommand();
        model.addAttribute("ingredient", ingredientCommand);

        //init uom
        ingredientCommand.setUom(new UnitOfMeasureCommand());

        model.addAttribute("uomList",  unitOfMeasureService.listAllUoms().collectList().block());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId,
                                         @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id).block());

        model.addAttribute("uomList", unitOfMeasureService.listAllUoms().collectList().block());
        return "recipe/ingredient/ingredientform";
    }

    @PostMapping("recipe/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute IngredientCommand command){
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command).block();

        log.debug("saved ingredient id:" + savedCommand.getId());

        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
    public String deleteIngredient(@PathVariable String recipeId,
                                   @PathVariable String id){

        log.debug("deleting ingredient id:" + id);
        ingredientService.deleteById(recipeId, id).block();

        return "redirect:/recipe/" + recipeId + "/ingredients";
    }
}
