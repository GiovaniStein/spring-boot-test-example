package com.example.demo.common;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.domain.Planet;

public class PlanetConstants {
    public static final Planet PLANET = new Planet(null, "Planeta", "climate", "terrain");
    public static final Planet INVALID_PLANET = new Planet(null, "", "", "");

    public static final Planet TATOOINE = new Planet(1L, "Tatooine", "arid", "desert");
    public static final Planet ALDERAAN = new Planet(1L, "Alderaan", "temperate", "grassl");
    public static final Planet YAVINIV = new Planet(1L, "Yavin IV", "temperate", "terrain");

    public static final List<Planet> PLANETS = new ArrayList<>() {
        {
            add(TATOOINE);
            add(ALDERAAN);
            add(YAVINIV);
        }

    };
}
