package com.spring.recipegradle.services.reactive;

import com.spring.recipegradle.commands.UnitOfMeasureCommand;
import com.spring.recipegradle.converters.UnitOfMeasureToUnitOfMeasureCommand;
import com.spring.recipegradle.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class UnitOfMeasureReactiveServiceImpl implements UnitOfMeasureReactiveService {
    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
    private final UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;

    public UnitOfMeasureReactiveServiceImpl(UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository,
                                            UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand) {
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
        this.unitOfMeasureToUnitOfMeasureCommand = unitOfMeasureToUnitOfMeasureCommand;
    }

    @Override
    public Flux<UnitOfMeasureCommand> listAllUoms() {
        return unitOfMeasureReactiveRepository
                .findAll()
                .map(unitOfMeasureToUnitOfMeasureCommand::convert);

    }
}
