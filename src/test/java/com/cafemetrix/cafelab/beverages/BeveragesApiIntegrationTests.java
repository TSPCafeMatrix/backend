package com.cafemetrix.cafelab.beverages;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * US14 - Registro de bebidas.
 * Integración real: Spring Boot completo + H2 en memoria + seguridad JWT, sin mocks.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class BeveragesApiIntegrationTests {

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

    private static Map<String, Object> latteBody(String name, double price) {
        return Map.of(
                "name", name,
                "category", "Caliente",
                "description", "Espresso doble con leche texturizada",
                "salePrice", price,
                "supplies", List.of(
                        Map.of("name", "Café molido", "quantity", 18, "unit", "g", "unitCost", 0.10),
                        Map.of("name", "Leche entera", "quantity", 200, "unit", "ml", "unitCost", 0.005),
                        Map.of("name", "Vaso 12 oz", "quantity", 1, "unit", "unidad", "unitCost", 0.50)));
    }

    private long createBeverage(String bearer, Map<String, Object> body) throws Exception {
        var response = mockMvc.perform(post("/api/v1/beverages")
                        .header("Authorization", "Bearer " + bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }

    @Test
    @DisplayName("Registra una bebida con insumos y devuelve costo total y margen calculados")
    void createBeverage_returnsCostAndMargin() throws Exception {
        mockMvc.perform(post("/api/v1/beverages")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(latteBody("Latte", 12.00))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Latte"))
                .andExpect(jsonPath("$.supplies.length()").value(3))
                .andExpect(jsonPath("$.totalCost").value(3.30))
                .andExpect(jsonPath("$.margin").value(8.70))
                .andExpect(jsonPath("$.marginPercentage").value(72.5));
    }

    @Test
    @DisplayName("Lista, obtiene, actualiza y elimina bebidas del barista autenticado")
    void fullCrud() throws Exception {
        long id = createBeverage(token, latteBody("Latte", 12.00));

        mockMvc.perform(get("/api/v1/beverages").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        var updated = Map.of(
                "name", "Latte de avena", "category", "Caliente", "salePrice", 14.00,
                "supplies", List.of(
                        Map.of("name", "Café molido", "quantity", 18, "unit", "g", "unitCost", 0.10),
                        Map.of("name", "Bebida de avena", "quantity", 200, "unit", "ml", "unitCost", 0.012)));
        mockMvc.perform(put("/api/v1/beverages/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Latte de avena"))
                .andExpect(jsonPath("$.supplies.length()").value(2))
                .andExpect(jsonPath("$.totalCost").value(4.20));

        mockMvc.perform(get("/api/v1/beverages/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supplies[1].name").value("Bebida de avena"));

        mockMvc.perform(delete("/api/v1/beverages/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/beverages/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Rechaza bebida sin insumos con 400 y mensaje del dominio")
    void createBeverage_withoutSupplies_returns400() throws Exception {
        var body = Map.of("name", "Agua", "category", "Fría", "salePrice", 3.00, "supplies", List.of());
        mockMvc.perform(post("/api/v1/beverages")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La bebida debe tener al menos un insumo"));
    }

    @Test
    @DisplayName("Un barista no puede ver las bebidas de otro barista")
    void beveragesAreScopedToOwner() throws Exception {
        long id = createBeverage(token, latteBody("Latte privado", 12.00));
        String otherToken = registerAndSignIn();

        mockMvc.perform(get("/api/v1/beverages/" + id).header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/beverages").header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Sin token no se puede acceder a bebidas")
    void requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/beverages"))
                .andExpect(status().isUnauthorized());
    }
}
