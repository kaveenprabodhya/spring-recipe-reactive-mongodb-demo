package com.spring.recipegradle.controllers;

import com.spring.recipegradle.commands.RecipeCommand;
import com.spring.recipegradle.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
//import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class RecipeController {

    private static final String RECIPE_FORM_URL = "recipe/recipeform";
    private final RecipeService recipeService;

    private WebDataBinder webDataBinder;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        this.webDataBinder = webDataBinder;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model) {

        model.addAttribute("recipe", recipeService.findById(id));

        return "recipe/show";
    }

    @GetMapping("recipe/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());

        return "recipe/recipeform";
    }

    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return RECIPE_FORM_URL;
    }

    @PostMapping("recipe/new")
    public Mono<String> saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand command) {
//        webDataBinder.validate();
//        BindingResult bindingResult = webDataBinder.getBindingResult();
//
//        if(bindingResult.hasErrors()){
//
//            bindingResult.getAllErrors().forEach(objectError -> {
//                log.debug(objectError.toString());
//            });
//
//            return RECIPE_FORM_URL;
//        }
//
//        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command).block();
//
//        return "redirect:/recipe/" + savedCommand.getId() + "/show";

        return recipeService.saveRecipeCommand(command)
                .flatMap(recipeCommand -> {
                    return Mono.just("redirect:/recipe/" + recipeCommand.getId() + "/show");
                }).onErrorResume(throwable -> {
                    return Mono.just(RECIPE_FORM_URL);
                });
    }

    @GetMapping("recipe/{id}/delete")
    public Mono<String> deleteById(@PathVariable String id) {

        log.debug("Deleting id: " + id);

        return recipeService.deleteById(id)
                .flatMap(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return Mono.just("redirect:/");
                    }
                    return Mono.just("redirect:/400error");
                }).onErrorResume(throwable -> {
                    return Mono.just("redirect:/400error");
                });
    }

//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(NotFoundException.class)
//    public ModelAndView handleNotFound(Exception exception){
//
//        log.error("Handling not found exception");
//        log.error(exception.getMessage());
//
//        ModelAndView modelAndView = new ModelAndView();
//
//        modelAndView.setViewName("404error");
//        modelAndView.addObject("exception", exception);
//
//        return modelAndView;
//    }

}
