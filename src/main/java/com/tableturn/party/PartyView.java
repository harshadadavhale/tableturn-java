
package com.tableturn.party;

public record PartyView(
        String partyId,
        String name,
        int covers,
        String state,
        int position,
        String joinedAt
) {}
