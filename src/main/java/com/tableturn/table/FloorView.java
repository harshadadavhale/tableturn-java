package com.tableturn.model;

import java.util.List;

public record FloorView(
        String branch,
        List<TableView> tables,
        long free
) {}