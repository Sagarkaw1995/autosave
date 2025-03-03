package com.cctns.autosave.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutoSaveRequestDto {

    @NotNull(message = "The Key Is Mandatory")
    private String key;
    @NotNull(message = "The Json Data Cannot Be NULL")
    private LinkedHashMap<String,Object> jsonData;
}
