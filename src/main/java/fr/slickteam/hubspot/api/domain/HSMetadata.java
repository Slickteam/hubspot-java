package fr.slickteam.hubspot.api.domain;

import java.math.BigDecimal;

public class HSMetadata extends HSObject {
    private static final String IS_CLOSED = "isClosed";
    private static final String PROBABILITY = "probability";

    public HSMetadata() {
    }

    public HSMetadata(boolean isClosed, BigDecimal probability) {
        this.setIsClosed(isClosed);
        this.setProbability(probability);
    }

    public boolean getIsClosed() {
        return getBooleanProperty(IS_CLOSED);
    }

    public HSMetadata setIsClosed(boolean isClosed) {
        setProperty(IS_CLOSED, Boolean.toString(isClosed));
        return this;
    }

    public BigDecimal getProbability() {
        return getBigDecimalProperty(PROBABILITY);
    }

    public HSMetadata setProbability(BigDecimal probability) {
        setProperty(IS_CLOSED, String.valueOf(probability));
        return this;
    }
}


