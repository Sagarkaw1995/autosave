package com.cctns.autosave.producer.service.mapper;

import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.web.dto.request.BailAutosaveRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.CrimeAutosaveRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.FinalFormAutosaveRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.FirAutosaveRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.GenericAutosaveRequest;
import com.cctns.autosave.producer.service.web.dto.request.MlcAutosaveRequestDto;
import com.cctns.autosave.producer.service.web.dto.request.ModuleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

/**
 * Contract for mapping Web Layer DTOs to Domain Layer or vice versa for the Malkhana Module.
 * <p>
 * This mapper facilitates the transformation of incoming request payloads into
 * business domain objects or from business domain objects to response objects.
 * </p>
 *
 * @see CycleAvoidingMappingContext
 */
@Mapper(componentModel = "spring")
public interface WebDomainMapper {

    /**
     * Maps module request {@link ModuleRequest}
     * to Autosave domain {@link AutosaveDomain}
     * @param moduleRequest module request {@link ModuleRequest}
     * @return Autosave domain {@link AutosaveDomain}
     */
    @SubclassMapping(source = FirAutosaveRequestDto.class,  target = AutosaveDomain.class)
    @SubclassMapping(source = BailAutosaveRequestDto.class, target = AutosaveDomain.class)
    @SubclassMapping(source = MlcAutosaveRequestDto.class,  target = AutosaveDomain.class)
    @SubclassMapping(source = FinalFormAutosaveRequestDto.class,  target = AutosaveDomain.class)
    @SubclassMapping(source = CrimeAutosaveRequestDto.class, target = AutosaveDomain.class)
    @SubclassMapping(source = GenericAutosaveRequest.class, target = AutosaveDomain.class)
    AutosaveDomain mapsModuleRequestToAutosaveDomain(ModuleRequest moduleRequest);
}
