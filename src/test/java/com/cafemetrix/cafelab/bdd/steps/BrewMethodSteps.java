package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.brewmethods.domain.model.aggregates.BrewMethod;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.CreateBrewMethodCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class BrewMethodSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int} listo para registrar métodos de preparación")
    public void unBaristaListoParaRegistrarMetodos(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        // El servicio se simula, pero construye el aggregate real: las reglas del dominio se ejecutan.
        when(shared.brewMethodCommandService.handle(any(CreateBrewMethodCommand.class)))
                .thenAnswer(inv -> new BrewMethod(inv.<CreateBrewMethodCommand>getArgument(0)));
    }

    @When("registra el método {string} de tipo {string} con {int} g de café, {int} ml de agua a {int} °C durante {int} segundos")
    public void registraElMetodo(String nombre, String tipo, int dosis, int agua, int temperatura, int segundos)
            throws Exception {
        var body = Map.of(
                "name", nombre, "method", tipo, "doseGrams", dosis, "waterMl", agua,
                "waterTemperatureC", temperatura, "brewTimeSeconds", segundos);
        shared.lastResult = mockMvc.perform(post("/api/v1/brew-methods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("el ratio del método es {string}")
    public void elRatioDelMetodoEs(String ratio) throws Exception {
        shared.lastResult.andExpect(jsonPath("$.ratioLabel").value(ratio));
    }
}
