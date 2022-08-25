package com.spring.recipegradle.services;

import com.spring.recipegradle.domain.Recipe;
import com.spring.recipegradle.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {
    private final RecipeReactiveRepository recipeRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Mono<ResponseEntity<Mono<Recipe>>> saveImageFile(String recipeId, MultipartFile file) {
        return recipeRepository.findById(recipeId)
                .map(recipe -> {
                    Byte[] byteObjects = new Byte[0];
                    try {
                        byteObjects = new Byte[file.getBytes().length];

                        int i = 0;

                        for (byte b : file.getBytes()) {
                            byteObjects[i++] = b;
                        }

                        recipe.setImage(byteObjects);
                    } catch (IOException e) {
                        log.error("Error occurred", e);
                        e.printStackTrace();
                    }
                        return recipe;
                }).map(recipeRepository::save).map(x -> new ResponseEntity<>(x, HttpStatus.CREATED));
    }
}
