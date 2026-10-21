package com.tableturn.controller;

import org.springframework.web.bind.annotation.*;

import com.tableturn.model.FloorView;
import com.tableturn.model.Section;
import com.tableturn.model.TableState;
import com.tableturn.model.TableView;
import com.tableturn.service.TableService;

@RestController
@RequestMapping("/branches/{branch}/tables")
public class TableController {

    private final TableService service;

    public TableController(TableService service) {
        this.service = service;
    }

    @GetMapping
    public FloorView getFloor(
            @PathVariable String branch,
            @RequestParam(required = false) Section section,
            @RequestParam(required = false) TableState state) {

        return service.getFloor(branch, section, state);
    }

    @GetMapping("/{no}")
    public TableView getTable(
            @PathVariable String branch,
            @PathVariable String no) {

        return service.getTable(branch, no);
    }
}