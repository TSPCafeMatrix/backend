package com.cafemetrix.cafelab.bdd.runner;

import io.cucumber.core.cli.Main;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ejecuta todos los escenarios BDD (src/test/resources/features) dentro de "mvn test".
 *
 * Por qué existe: Surefire no reporta los escenarios lanzados vía {@link CucumberTestSuite}
 * (muestra "Tests run: 0" y BUILD SUCCESS aunque haya escenarios fallando). Este test corre
 * Cucumber por su API y falla si algún escenario falla, así el build refleja el estado real.
 * {@link CucumberTestSuite} se mantiene para ejecutar los features desde el IDE.
 */
class CucumberBddTests {

    @Test
    @DisplayName("Todos los escenarios BDD de los features pasan")
    void allFeaturesPass() {
        byte exitStatus = Main.run(
                new String[] {
                    "--glue", "com.cafemetrix.cafelab.bdd",
                    "--plugin", "summary",
                    "--plugin", "progress",
                    "classpath:features"
                },
                Thread.currentThread().getContextClassLoader());

        assertEquals(0, exitStatus, "Hay escenarios BDD fallando: revisar el resumen de Cucumber arriba");
    }
}
