package com.cafemetrix.cafelab.brewmethods.interfaces.rest;

import com.cafemetrix.cafelab.brewmethods.domain.exceptions.BrewMethodNotFoundException;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.DeleteBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.model.queries.GetBrewMethodByIdForUserQuery;
import com.cafemetrix.cafelab.brewmethods.domain.model.queries.GetBrewMethodsByUserIdQuery;
import com.cafemetrix.cafelab.brewmethods.domain.services.BrewMethodCommandService;
import com.cafemetrix.cafelab.brewmethods.domain.services.BrewMethodQueryService;
import com.cafemetrix.cafelab.brewmethods.interfaces.rest.resources.BrewMethodResource;
import com.cafemetrix.cafelab.brewmethods.interfaces.rest.resources.SaveBrewMethodResource;
import com.cafemetrix.cafelab.brewmethods.interfaces.rest.transform.BrewMethodCommandFromResourceAssembler;
import com.cafemetrix.cafelab.brewmethods.interfaces.rest.transform.BrewMethodResourceFromEntityAssembler;
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
 * US13 - Registro de métodos de preparación.
 * Todas las operaciones se limitan a los métodos del perfil autenticado (userId desde el JWT).
 */
@RestController
@RequestMapping(value = "/api/v1/brew-methods", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Brew methods", description = "Métodos de preparación estándar con dosis, agua, temperatura y tiempo")
public class BrewMethodsController {

    private final BrewMethodCommandService commandService;
    private final BrewMethodQueryService queryService;
    private final CurrentProfileIdResolver currentProfileIdResolver;

    public BrewMethodsController(
            BrewMethodCommandService commandService,
            BrewMethodQueryService queryService,
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
    @Operation(summary = "Registrar método de preparación")
    public ResponseEntity<?> create(@RequestBody SaveBrewMethodResource resource) {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        var created = commandService.handle(BrewMethodCommandFromResourceAssembler.toCreateCommand(userId, resource));
        return new ResponseEntity<>(BrewMethodResourceFromEntityAssembler.toResourceFromEntity(created), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar métodos de preparación del perfil autenticado")
    public ResponseEntity<?> list() {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        List<BrewMethodResource> resources = queryService.handle(new GetBrewMethodsByUserIdQuery(userId)).stream()
                .map(BrewMethodResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{brewMethodId}")
    @Operation(summary = "Obtener método por id (solo del propio perfil)")
    public ResponseEntity<?> getById(@PathVariable Long brewMethodId) {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        var method = queryService.handle(new GetBrewMethodByIdForUserQuery(brewMethodId, userId))
                .orElseThrow(() -> new BrewMethodNotFoundException(brewMethodId));
        return ResponseEntity.ok(BrewMethodResourceFromEntityAssembler.toResourceFromEntity(method));
    }

    @PutMapping(value = "/{brewMethodId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar método (solo del propio perfil)")
    public ResponseEntity<?> update(@PathVariable Long brewMethodId, @RequestBody SaveBrewMethodResource resource) {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        var command = BrewMethodCommandFromResourceAssembler.toUpdateCommand(brewMethodId, userId, resource);
        var updated = commandService.handle(command).orElseThrow(() -> new BrewMethodNotFoundException(brewMethodId));
        return ResponseEntity.ok(BrewMethodResourceFromEntityAssembler.toResourceFromEntity(updated));
    }

    @DeleteMapping("/{brewMethodId}")
    @Operation(summary = "Eliminar método (solo del propio perfil)")
    public ResponseEntity<?> delete(@PathVariable Long brewMethodId) {
        Long userId = currentUserIdOrNull();
        if (userId == null) return unauthorized();
        if (!commandService.handle(new DeleteBrewMethodCommand(brewMethodId, userId))) {
            throw new BrewMethodNotFoundException(brewMethodId);
        }
        return ResponseEntity.ok(new MessageResource("Método de preparación eliminado exitosamente"));
    }
}
