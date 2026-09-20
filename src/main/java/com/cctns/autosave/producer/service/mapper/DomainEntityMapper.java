package com.cctns.autosave.producer.service.mapper;

import org.mapstruct.Mapper;

/**
 * Contract for mapping  Domain Layer to Entity or vice versa for the Malkhana Module.
 * <p>
 * This mapper facilitates the transformation of business domain objects into
 * database entities or from database entities to business domain objects.
 * </p>
 *
 * @see CycleAvoidingMappingContext
 */
@Mapper(componentModel = "spring")
public interface DomainEntityMapper {

}
