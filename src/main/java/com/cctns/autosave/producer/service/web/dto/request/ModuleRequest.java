package com.cctns.autosave.producer.service.web.dto.request;

import com.cctns.autosave.producer.service.constants.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Every module's request payload implements this.
 * The interface itself has no business fields — it only carries the
 * Jackson annotations that tell the deserializer which concrete class
 * to build, based purely on the "module" value in the incoming JSON.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "module",
        visible = true,
        defaultImpl = GenericAutosaveRequest.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FirAutosaveRequestDto.class, name = Constants.FIR_MODULE_NAME_VALIDATION),
        @JsonSubTypes.Type(value = BailAutosaveRequestDto.class, name = Constants.BAIL_CANCEL_MODULE),
        @JsonSubTypes.Type(value = ArrestAutosaveRequestDto.class, name = Constants.ARREST_WARRANT_MODULE),
        @JsonSubTypes.Type(value = MlcAutosaveRequestDto.class, name = Constants.MLC_MODULE_NAME_VALIDATION),
        @JsonSubTypes.Type(value = FinalFormAutosaveRequestDto.class, name = Constants.FINAL_FORM_MODULE_NAME_VALIDATION),
        @JsonSubTypes.Type(value = CrimeAutosaveRequestDto.class, name = Constants.CRIME_MODULE)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class ModuleRequest extends CommonParamsDTO {
    @NotNull(message = "Module Name Is Mandatory")
    @JsonProperty("module")
    private String moduleName;
}
