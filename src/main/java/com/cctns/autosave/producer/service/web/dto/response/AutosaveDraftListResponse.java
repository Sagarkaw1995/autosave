package com.cctns.autosave.producer.service.web.dto.response;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutosaveDraftListResponse {
    List<LinkedHashMap<String, Object>> draftList;
}
