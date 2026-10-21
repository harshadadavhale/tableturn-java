
package com.tableturn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tableturn.model.FloorView;
import com.tableturn.model.Section;
import com.tableturn.model.TableState;
import com.tableturn.model.TableView;
import com.tableturn.repository.TableRepository;

@Service
public class TableService {

    private final TableRepository repository;

    public TableService(TableRepository repository) {
        this.repository = repository;
    }

    public FloorView getFloor(
            String branch, Section section, TableState state) {

        checkBranch(branch);

        List<TableView> filtered = repository.findAll().stream()
                .filter(t -> section == null || t.section() == section)
                .filter(t -> state == null || t.state() == state)
                .toList();

        long free = filtered.stream()
                .filter(t -> t.state() == TableState.free)
                .count();

        return new FloorView("Baner", filtered, free);
    }

    public TableView getTable(String branch, String no) {
        checkBranch(branch);

        return repository.findByNo(no)
                .orElseThrow(() -> new NoSuchTableException(no));
    }

    private void checkBranch(String branch) {
        if (!"Baner".equalsIgnoreCase(branch)) {
            throw new NoSuchBranchException(branch);
        }
    }

    public static class NoSuchBranchException extends RuntimeException {
        public NoSuchBranchException(String branch) {
            super("No such branch: " + branch);
        }
    }

    public static class NoSuchTableException extends RuntimeException {
        public NoSuchTableException(String no) {
            super("No such table: " + no);
        }
    }
}
