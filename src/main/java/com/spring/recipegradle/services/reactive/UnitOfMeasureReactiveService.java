package com.spring.recipegradle.services.reactive;

import com.spring.recipegradle.commands.UnitOfMeasureCommand;
import reactor.core.publisher.Flux;

public interface UnitOfMeasureReactiveService {
    Flux<UnitOfMeasureCommand> listAllUoms();
}
