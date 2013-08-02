package ca.bsolomon.gw2events.quick.util;

public enum EventStatusValues {
	ACTIVE("Active"),
    PREPARATION("Preparation"),
    SUCCESS("Success"),
    FAIL("Fail"),
    WARMUP("Warmup")
    ;

    private final String text;
    
	private EventStatusValues(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
