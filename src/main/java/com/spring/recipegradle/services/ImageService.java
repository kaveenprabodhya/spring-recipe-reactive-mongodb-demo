package com.spring.recipegradle.services;

import com.spring.recipegradle.domain.Recipe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface ImageService {
    Mono<ResponseEntity<Mono<Recipe>>> saveImageFile(String recipeId, MultipartFile file);
}
