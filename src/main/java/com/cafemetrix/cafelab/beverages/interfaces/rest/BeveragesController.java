package com.cafemetrix.cafelab.beverages.interfaces.rest;

import com.cafemetrix.cafelab.beverages.domain.exceptions.BeverageNotFoundException;
import com.cafemetrix.cafelab.beverages.domain.model.commands.DeleteBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.queries.GetBeverageByIdForUserQuery;
import com.cafemetrix.cafelab.beverages.domain.model.queries.GetBeveragesByUserIdQuery;
import com.cafemetrix.cafelab.beverages.domain.services.BeverageCommandService;
import com.cafemetrix.cafelab.beverages.domain.services.BeverageQueryService;
import com.cafemetrix.cafelab.beverages.interfaces.rest.resources.BeverageResource;
import com.cafemetrix.cafelab.beverages.interfaces.rest.resources.SaveBeverageResource;
import com.cafemetrix.cafelab.beverages.interfaces.rest.transform.BeverageCommandFromResourceAssembler;
import com.cafemetrix.cafelab.beverages.interfaces.rest.transform.BeverageResourceFromEntityAssembler;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * US14 - Registro de bebidas con insumos y precio de venta.
 * Todas las operaciones se limitan a las bebidas del perfil autenticado (userId desde el JWT).
 */
@RestController
@RequestMapping(value = "/api/v1/beverages", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Beverages", description = "Registro de bebidas con insumos, costo y margen")
public class BeveragesController {

    private final BeverageCommandService commandService;
    private final BeverageQueryService queryService;
    private final CurrentProfileIdResolver currentProfileIdResolver;

    public BeveragesController(
            BeverageCommandService commandService,
            BeverageQueryService queryService,
            CurrentProfileIdResolver currentProfileIdResolver) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.currentProfileIdResolver = currentProfileIdResolver;
    }

    private Long currentUserIdOrNull() {
        return currentProfileIdResolver.resolveProfileId().orElse(null);
    }

    private static ResponseEntity<MessageResource> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResource("Usuario no autenticado o perfil no encontrado"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Registrar bebida con insumos y precio de venta")
    public ResponseEntity<?> create(@RequestBody SaveBeverageResource resource) {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        var command = BeverageCommandFromResourceAssembler.toCreateCommand(userId, resource);
        var created = commandService.handle(command);
        return new ResponseEntity<>(BeverageResourceFromEntityAssembler.toResourceFromEntity(created), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar bebidas del perfil autenticado")
    public ResponseEntity<?> list() {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        List<BeverageResource> resources = queryService.handle(new GetBeveragesByUserIdQuery(userId)).stream()
                .map(BeverageResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{beverageId}")
    @Operation(summary = "Obtener bebida por id (solo del propio perfil)")
    public ResponseEntity<?> getById(@PathVariable Long beverageId) {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        var beverage = queryService.handle(new GetBeverageByIdForUserQuery(beverageId, userId))
                .orElseThrow(() -> new BeverageNotFoundException(beverageId));
        return ResponseEntity.ok(BeverageResourceFromEntityAssembler.toResourceFromEntity(beverage));
    }

    @PutMapping(value = "/{beverageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar bebida, sus insumos y precio (solo del propio perfil)")
    public ResponseEntity<?> update(@PathVariable Long beverageId, @RequestBody SaveBeverageResource resource) {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        var command = BeverageCommandFromResourceAssembler.toUpdateCommand(beverageId, userId, resource);
        var updated = commandService.handle(command)
                .orElseThrow(() -> new BeverageNotFoundException(beverageId));
        return ResponseEntity.ok(BeverageResourceFromEntityAssembler.toResourceFromEntity(updated));
    }

    @DeleteMapping("/{beverageId}")
    @Operation(summary = "Eliminar bebida (solo del propio perfil)")
    public ResponseEntity<?> delete(@PathVariable Long beverageId) {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        if (!commandService.handle(new DeleteBeverageCommand(beverageId, userId))) {
            throw new BeverageNotFoundException(beverageId);
        }
        return ResponseEntity.ok(new MessageResource("Bebida eliminada exitosamente"));
    }
}
