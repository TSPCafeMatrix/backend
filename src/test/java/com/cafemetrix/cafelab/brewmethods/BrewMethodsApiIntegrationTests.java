package com.cafemetrix.cafelab.brewmethods;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * US13 - Registro de métodos de preparación.
 * Integración real: Spring Boot completo + H2 en memoria + seguridad JWT, sin mocks.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class BrewMethodsApiIntegrationTests {

    private static final String PASSWORD = "Barista#2026";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = registerAndSignIn();
    }

    private String registerAndSignIn() throws Exception {
        var email = "barista-" + UUID.randomUUID() + "@cafelab.test";
        var profile = Map.of(
                "name", "Barista Test", "email", email, "password", PASSWORD, "role", "barista",
                "isFirstLogin", false, "hasPlan", true, "plan", "basic");
        mockMvc.perform(post("/api/v1/profiles").contentType(MediaType.APPLICATION_JSON).content(json(profile)))
                .andExpect(status().isCreated());
        var response = mockMvc.perform(post("/api/v1/authentication/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private static Map<String, Object> v60(Long calibrationId) {
        var body = new HashMap<String, Object>();
        body.put("name", "V60 de la casa");
        body.put("method", "v60");
        body.put("equipment", "Hario V60 02");
        body.put("doseGrams", 15);
        body.put("waterMl", 250);
        body.put("waterTemperatureC", 93);
        body.put("brewTimeSeconds", 180);
        body.put("grindCalibrationId", calibrationId);
        body.put("notes", "Bloom de 30 s");
        return body;
    }

    private long createCalibration(String bearer) throws Exception {
        var calibration = Map.of(
                "name", "Molienda media", "method", "V60", "equipment", "Comandante C40", "grindNumber", "22",
                "aperture", 0.8, "cupVolume", 250, "finalVolume", 230,
                "calibrationDate", LocalDate.now().toString());
        var response = mockMvc.perform(post("/api/v1/calibrations")
                        .header("Authorization", "Bearer " + bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(calibration)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private long createMethod(String bearer, Map<String, Object> body) throws Exception {
        var response = mockMvc.perform(post("/api/v1/brew-methods")
                        .header("Authorization", "Bearer " + bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("Registra un método vinculado a una calibración propia y devuelve el ratio")
    void create_withOwnCalibration_returnsRatio() throws Exception {
        long calibrationId = createCalibration(token);

        mockMvc.perform(post("/api/v1/brew-methods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(v60(calibrationId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.method").value("v60"))
                .andExpect(jsonPath("$.ratio").value(16.7))
                .andExpect(jsonPath("$.ratioLabel").value("1:16.7"))
                .andExpect(jsonPath("$.grindCalibrationId").value(calibrationId));
    }

    @Test
    @DisplayName("Rechaza vincular una calibración que no existe o es de otro barista")
    void create_withForeignCalibration_returns400() throws Exception {
        String otherToken = registerAndSignIn();
        long foreignCalibrationId = createCalibration(otherToken);

        for (long id : new long[] {foreignCalibrationId, 999_999L}) {
            mockMvc.perform(post("/api/v1/brew-methods")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(v60(id))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("La calibración de molienda indicada no existe"));
        }
    }

    @Test
    @DisplayName("Lista, actualiza y elimina métodos del barista autenticado")
    void fullCrud() throws Exception {
        long id = createMethod(token, v60(null));

        mockMvc.perform(get("/api/v1/brew-methods").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        var updated = v60(null);
        updated.put("name", "V60 intenso");
        updated.put("doseGrams", 20);
        updated.put("waterMl", 300);
        mockMvc.perform(put("/api/v1/brew-methods/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("V60 intenso"))
                .andExpect(jsonPath("$.ratioLabel").value("1:15.0"));

        mockMvc.perform(delete("/api/v1/brew-methods/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/brew-methods/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Rechaza un método con agua menor o igual a la dosis")
    void create_invalidRatio_returns400() throws Exception {
        var body = v60(null);
        body.put("waterMl", 10);
        mockMvc.perform(post("/api/v1/brew-methods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La cantidad de agua debe ser mayor que la dosis de café"));
    }

    @Test
    @DisplayName("Un barista no ve los métodos de otro y sin token no hay acceso")
    void scopedToOwnerAndAuthenticated() throws Exception {
        long id = createMethod(token, v60(null));
        String otherToken = registerAndSignIn();

        mockMvc.perform(get("/api/v1/brew-methods/" + id).header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/brew-methods"))
                .andExpect(status().isUnauthorized());
    }
}
