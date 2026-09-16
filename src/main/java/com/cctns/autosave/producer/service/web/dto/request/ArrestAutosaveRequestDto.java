package com.cctns.autosave.producer.service.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArrestAutosaveRequestDto extends ModuleRequest {
    private Long accusedVid;
    //   @NotNull(message = "Accused Serial Number Cannot Be Null Arrest Autosave") : Later switch to this
    private Long accusedSrno;
    private String accusedName;
}
