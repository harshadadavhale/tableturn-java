
package com.tableturn.error;

public class PartyTooBig extends RuntimeException {

    private final int maxCovers;

    public PartyTooBig(int covers, int maxCovers) {
        super("Party size " + covers + " exceeds maximum of " + maxCovers);
        this.maxCovers = maxCovers;
    }

    public int getMaxCovers() {
        return maxCovers;
    }
}
