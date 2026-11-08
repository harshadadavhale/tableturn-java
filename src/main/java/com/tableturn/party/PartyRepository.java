
package com.tableturn.party;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class PartyRepository {

    private final JdbcTemplate jdbc;

    public PartyRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int nextPosition() {
        Integer position = jdbc.queryForObject(
                "SELECT COUNT(*) + 1 FROM parties WHERE state = ?",
                Integer.class,
                "waiting"
        );
        return position == null ? 1 : position;
    }

    
public void save(JoinResponse party) {
    jdbc.update(
            """
            INSERT INTO parties
            (party_id, branch_id, name, covers, phone,
             position, state, joined_at)
            VALUES (?, 1, ?, ?, ?, ?, ?, ?)
            """,
            party.partyId(),
            party.name(),
            party.covers(),
            party.phone(),
            party.position(),
            party.state(),
            Timestamp.from(Instant.parse(party.joinedAt()))
    );
}


    public Optional<PartyView> findByPartyId(String partyId) {
        List<PartyView> results = jdbc.query(
                """
                SELECT party_id, name, covers, state, position, joined_at
                FROM parties
                WHERE party_id = ?
                """,
                (rs, rowNum) -> new PartyView(
                        rs.getString("party_id"),
                        rs.getString("name"),
                        rs.getInt("covers"),
                        rs.getString("state"),
                        rs.getInt("position"),
                        rs.getTimestamp("joined_at").toInstant().toString()
                ),
                partyId
        );

        return results.stream().findFirst();
    }
}
