package org.bayasystems.svimporter.model;

public enum SVArtifactType {
    REQUIREMENT("req"),
    DESIGN("dsn"),
    IMPLEMENTATION("impl"),
    UNIT_TEST("utest"),
    INTEGRATION_TEST("itest"),
    SYSTEM_TEST("stest");

    private final String type;

    SVArtifactType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}