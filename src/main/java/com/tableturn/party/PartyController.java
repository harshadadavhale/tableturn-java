
package com.tableturn.party;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class PartyController {

    private final PartyService service;

    public PartyController(PartyService service) {
        this.service = service;
    }

    @PostMapping("/waitlist")
    public ResponseEntity<JoinResponse> join(
            @Valid @RequestBody JoinRequest request
    ) {
        JoinResponse response = service.join(request);

        return ResponseEntity
                .created(URI.create("/parties/" + response.partyId()))
                .body(response);
    }

    @GetMapping("/parties/{partyId}")
    public ResponseEntity<PartyView> getParty(
            @PathVariable String partyId
    ) {
        return ResponseEntity.ok(service.getParty(partyId));
    }
}
