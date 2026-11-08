
package com.tableturn.party;

public record JoinResponse(
        String partyId,
        int position,
        String name,
        int covers,
        String phone,
        String state,
        String joinedAt
) {}
