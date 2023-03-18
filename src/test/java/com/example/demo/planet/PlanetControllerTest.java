package com.example.demo.planet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.common.PlanetConstants;
import com.example.demo.controller.PlanetController;
import com.example.demo.domain.Planet;
import com.example.demo.service.PlanetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private PlanetService planetService;

    @Test
    public void createPlanetWithValidData() throws JsonProcessingException, Exception {
        when(planetService.create(PlanetConstants.PLANET)).thenReturn(PlanetConstants.PLANET);

        mockMvc.perform(MockMvcRequestBuilders.post("/planets")
                .content(objectMapper.writeValueAsString(PlanetConstants.PLANET)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(PlanetConstants.PLANET));
    }

    @Test
    public void createPlanetWithInvalidData() throws JsonProcessingException, Exception {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/planets")
                .content(objectMapper.writeValueAsString(emptyPlanet))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        mockMvc.perform(MockMvcRequestBuilders.post("/planets")
                .content(objectMapper.writeValueAsString(invalidPlanet))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    public void createPlanetWithExistingName() throws JsonProcessingException, Exception {
        when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/planets")
                .content(objectMapper.writeValueAsString(PlanetConstants.PLANET))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void getPlanetByExistingId() throws JsonProcessingException, Exception {
        when(planetService.get(1L)).thenReturn(Optional.of(PlanetConstants.PLANET));

        mockMvc.perform(MockMvcRequestBuilders.get("/planets/1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(PlanetConstants.PLANET));
    }

    @Test
    public void getPlanetByUnexistingId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/planets/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getPlanetByExistingName() throws Exception {

        when(planetService.getByName(PlanetConstants.PLANET.getName())).thenReturn(Optional.of(PlanetConstants.PLANET));

        mockMvc.perform(MockMvcRequestBuilders.get("/planets/name/" + PlanetConstants.PLANET.getName()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value(PlanetConstants.PLANET));
    }

    @Test
    public void getPlanetByUnexistingName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/planets/name/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void listPlanetsFilteredPlanets() throws Exception {
        when(planetService.list(null, null)).thenReturn(PlanetConstants.PLANETS);
        when(planetService.list(PlanetConstants.TATOOINE.getTerrain(), PlanetConstants.TATOOINE.getClimate())).thenReturn(List.of(PlanetConstants.TATOOINE));

        mockMvc.perform(MockMvcRequestBuilders.get("/planets"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));

        mockMvc.perform(MockMvcRequestBuilders.get("/planets?" + String.format("terrain=%s&climate=%s", PlanetConstants.TATOOINE.getTerrain(), PlanetConstants.TATOOINE.getClimate())))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value(PlanetConstants.TATOOINE));
    }

    @Test
    public void listPlanetsNoPlanets() throws Exception {
        when(planetService.list(null, null)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/planets"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void removePlanetWithExistingId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/planets/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void removePlanetWithUnexistingId() throws Exception {
        final Long planetId = 1L;
        doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(planetId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/planets/" + planetId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
