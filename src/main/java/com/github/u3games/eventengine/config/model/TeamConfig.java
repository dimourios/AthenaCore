package com.github.u3games.eventengine.config.model;

import com.github.u3games.eventengine.enums.TeamType;
import com.github.u3games.eventengine.model.ELocation;

import java.util.List;

public class TeamConfig {

    private String name;
    private String color;
    private List<ELocation> locations;

    public String getName() {
        return name;
    }

    public TeamType getColor() {
        return TeamType.getType(color);
    }

    public List<ELocation> getLocations() {
        return locations;
    }
}
