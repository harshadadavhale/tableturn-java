
package com.tableturn.error;

public class NoSuchParty extends RuntimeException {

    public NoSuchParty(String partyId) {
        super("No such party: " + partyId);
    }
}
