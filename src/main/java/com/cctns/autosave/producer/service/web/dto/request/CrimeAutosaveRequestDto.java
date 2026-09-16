package com.cctns.autosave.producer.service.web.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrimeAutosaveRequestDto extends ModuleRequest{
    @NotNull(message = "Fir Registration Number Cannot Be Null For Crime Detail Autosave")
    private Long firRegNum;
    private LocalDateTime regDate;
}
