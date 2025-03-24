package org.bayasystems.svimporter.model;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;

public class SVSpecificationItem {
    private final SpecificationItemId id;
    private final String title;
    private final String description;
    private final Location location;

    public SVSpecificationItem(SpecificationItemId id, String title, String description, Location location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
    }

    public SpecificationItemId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }
}