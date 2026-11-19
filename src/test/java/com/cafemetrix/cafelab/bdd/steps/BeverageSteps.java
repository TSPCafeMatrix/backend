package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.beverages.domain.model.aggregates.Beverage;
import com.cafemetrix.cafelab.beverages.domain.model.commands.CreateBeverageCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class BeverageSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int} listo para registrar bebidas")
    public void unBaristaAutenticadoListoParaRegistrarBebidas(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        // El servicio se simula, pero construye el aggregate real: las reglas del dominio se ejecutan.
        when(shared.beverageCommandService.handle(any(CreateBeverageCommand.class)))
                .thenAnswer(inv -> new Beverage(inv.<CreateBeverageCommand>getArgument(0)));
    }

    @When("registra la bebida {string} de categoría {string} con precio {double} y los insumos:")
    public void registraLaBebida(String nombre, String categoria, double precio, DataTable insumos) throws Exception {
        List<Map<String, Object>> supplies = insumos.asMaps().stream()
                .map(row -> {
                    Map<String, Object> s = new LinkedHashMap<>();
                    s.put("name", row.get("nombre"));
                    s.put("quantity", new BigDecimal(row.get("cantidad")));
                    s.put("unit", row.get("unidad"));
                    s.put("unitCost", new BigDecimal(row.get("costoUnitario")));
                    return s;
                })
                .toList();
        var body = Map.of("name", nombre, "category", categoria, "salePrice", precio, "supplies", supplies);
        shared.lastResult = mockMvc.perform(post("/api/v1/beverages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("el costo total de la bebida es {double}")
    public void elCostoTotalEs(double costo) throws Exception {
        shared.lastResult.andExpect(jsonPath("$.totalCost").value(costo));
    }

    @And("el margen de la bebida es {double} con un porcentaje de {double}")
    public void elMargenEs(double margen, double porcentaje) throws Exception {
        shared.lastResult
                .andExpect(jsonPath("$.margin").value(margen))
                .andExpect(jsonPath("$.marginPercentage").value(porcentaje));
    }

    @And("el mensaje de error es {string}")
    public void elMensajeDeErrorEs(String mensaje) throws Exception {
        shared.lastResult.andExpect(jsonPath("$.message").value(mensaje));
    }
}
