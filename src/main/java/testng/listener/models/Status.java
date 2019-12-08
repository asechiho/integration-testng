package testng.listener.models;

import testng.listener.config.IntegrationConfig;

public enum Status {
    PASS,FAIL,SKIPP;

    public String getStatus() {
        IntegrationConfig config = IntegrationConfig.getInstance();
        switch (this) {
            case PASS: return config.getStatusPass();
            case FAIL: return config.getStatusFail();
            case SKIPP: return config.getStatusSkip();
            default:
                throw new IllegalArgumentException("Switch value was not found. Value: " + this.name());
        }
    }
}
