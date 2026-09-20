package com.cctns.autosave.producer.service.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pageable {

    @NotNull(message = "Page Number is mandatory")
    private Integer page;

    @NotNull(message = "Page size is mandatory")
    private Integer pageSize;
}
