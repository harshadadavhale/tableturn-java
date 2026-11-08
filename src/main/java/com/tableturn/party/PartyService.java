
package com.tableturn.party;

import com.tableturn.error.NoSuchParty;
import com.tableturn.error.PartyTooBig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class PartyService {

    private final PartyRepository repository;
    private final PartyIds ids;
    private final Clock clock;

    public PartyService(
            PartyRepository repository,
            PartyIds ids,
            Clock clock
    ) {
        this.repository = repository;
        this.ids = ids;
        this.clock = clock;
    }

    @Transactional
    public JoinResponse join(JoinRequest request) {
        if (request.covers() > 6) {
            throw new PartyTooBig(request.covers(), 6);
        }

        int position = repository.nextPosition();
        String partyId = ids.nextId();
        String joinedAt = Instant.now(clock).toString();

        JoinResponse response = new JoinResponse(
                partyId,
                position,
                request.name().trim(),
                request.covers(),
                request.phone(),
                "waiting",
                joinedAt
        );

        repository.save(response);
        return response;
    }

    public PartyView getParty(String partyId) {
        return repository.findByPartyId(partyId)
                .orElseThrow(() -> new NoSuchParty(partyId));
    }
}
