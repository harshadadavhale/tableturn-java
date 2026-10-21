package com.tableturn.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TableView(
        String no,
        int seats,
        Section section,
        TableState state
) {}