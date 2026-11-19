# US14: Registro de Bebidas
# Como dueño de cafetería o barista,
# quiero registrar las bebidas de mi carta con sus insumos y precio de venta
# para conocer su costo y el margen que me deja cada una.

Feature: US14 - Registro de Bebidas

  Scenario: Barista registra una bebida con insumos y obtiene costo y margen
    Given un barista autenticado con perfil id 1 listo para registrar bebidas
    When registra la bebida "Latte" de categoría "Caliente" con precio 12.00 y los insumos:
      | nombre       | cantidad | unidad | costoUnitario |
      | Café molido  | 18       | g      | 0.10          |
      | Leche entera | 200      | ml     | 0.005         |
      | Vaso 12 oz   | 1        | unidad | 0.50          |
    Then el sistema responde con código 201
    And el costo total de la bebida es 3.30
    And el margen de la bebida es 8.70 con un porcentaje de 72.5

  Scenario: Registro rechazado cuando la bebida no tiene insumos
    Given un barista autenticado con perfil id 1 listo para registrar bebidas
    When registra la bebida "Agua" de categoría "Fría" con precio 3.00 y los insumos:
      | nombre | cantidad | unidad | costoUnitario |
    Then el sistema responde con código 400
    And el mensaje de error es "La bebida debe tener al menos un insumo"

  Scenario: Registro rechazado cuando un insumo tiene cantidad cero
    Given un barista autenticado con perfil id 1 listo para registrar bebidas
    When registra la bebida "Espresso" de categoría "Caliente" con precio 6.00 y los insumos:
      | nombre      | cantidad | unidad | costoUnitario |
      | Café molido | 0        | g      | 0.10          |
    Then el sistema responde con código 400
    And el mensaje de error es "La cantidad del insumo debe ser mayor a 0"

  Scenario: Usuario no autenticado intenta registrar una bebida
    Given un usuario no autenticado
    When registra la bebida "Latte" de categoría "Caliente" con precio 12.00 y los insumos:
      | nombre      | cantidad | unidad | costoUnitario |
      | Café molido | 18       | g      | 0.10          |
    Then el sistema responde con código 401
