package com.tableturn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.tableturn.model.Section;
import com.tableturn.model.TableState;
import com.tableturn.model.TableView;

@Repository
public class TableRepository {

    private final JdbcClient jdbc;

    public TableRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public long count() {
        return jdbc.sql("SELECT COUNT(*) FROM tables")
                .query(Long.class)
                .single();
    }

    public void insert(String no, int seats, Section section) {
        jdbc.sql("""
                INSERT INTO tables(no, seats, section, current_seating_id)
                VALUES (:no, :seats, :section, NULL)
                """)
            .param("no", no)
            .param("seats", seats)
            .param("section", section.name())
            .update();
    }

    public List<TableView> findAll() {
        return jdbc.sql("""
                SELECT t.no, t.seats, t.section,
                       CASE
                           WHEN s.id IS NULL THEN 'free'
                           ELSE s.state
                       END AS table_state
                FROM tables t
                LEFT JOIN seatings s
                  ON s.id = t.current_seating_id
                ORDER BY t.no
                """)
            .query((rs, rowNum) -> new TableView(
                    rs.getString("no"),
                    rs.getInt("seats"),
                    Section.valueOf(rs.getString("section")),
                    TableState.valueOf(rs.getString("table_state"))
            ))
            .list();
    }

    public Optional<TableView> findByNo(String no) {
        return jdbc.sql("""
                SELECT t.no, t.seats, t.section,
                       CASE
                           WHEN s.id IS NULL THEN 'free'
                           ELSE s.state
                       END AS table_state
                FROM tables t
                LEFT JOIN seatings s
                  ON s.id = t.current_seating_id
                WHERE t.no = :no
                """)
            .param("no", no)
            .query((rs, rowNum) -> new TableView(
                    rs.getString("no"),
                    rs.getInt("seats"),
                    Section.valueOf(rs.getString("section")),
                    TableState.valueOf(rs.getString("table_state"))
            ))
            .optional();
    }
}