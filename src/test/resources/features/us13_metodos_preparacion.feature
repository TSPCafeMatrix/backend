# US13: Registro de Métodos de Preparación
# Como barista profesional,
# quiero registrar mis métodos de preparación con dosis, agua, temperatura y tiempo
# para estandarizar la extracción y repetir los mismos resultados en cada taza.

Feature: US13 - Registro de Métodos de Preparación

  Scenario: Barista registra un método y obtiene el ratio calculado
    Given un barista autenticado con perfil id 1 listo para registrar métodos de preparación
    When registra el método "V60 de la casa" de tipo "v60" con 15 g de café, 250 ml de agua a 93 °C durante 180 segundos
    Then el sistema responde con código 201
    And el ratio del método es "1:16.7"

  Scenario: Registro rechazado cuando el agua no supera la dosis de café
    Given un barista autenticado con perfil id 1 listo para registrar métodos de preparación
    When registra el método "Error" de tipo "v60" con 20 g de café, 20 ml de agua a 93 °C durante 180 segundos
    Then el sistema responde con código 400
    And el mensaje de error es "La cantidad de agua debe ser mayor que la dosis de café"

  Scenario: Registro rechazado cuando el tipo de método no existe
    Given un barista autenticado con perfil id 1 listo para registrar métodos de preparación
    When registra el método "Raro" de tipo "microondas" con 15 g de café, 250 ml de agua a 93 °C durante 60 segundos
    Then el sistema responde con código 400
    And el mensaje de error es "Método de preparación no válido: microondas"

  Scenario: Usuario no autenticado intenta registrar un método
    Given un usuario no autenticado
    When registra el método "V60 de la casa" de tipo "v60" con 15 g de café, 250 ml de agua a 93 °C durante 180 segundos
    Then el sistema responde con código 401
