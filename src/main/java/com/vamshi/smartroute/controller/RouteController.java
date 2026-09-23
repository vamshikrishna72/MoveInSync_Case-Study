package com.vamshi.smartroute.controller;

import com.vamshi.smartroute.dto.*;
import com.vamshi.smartroute.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/routes")
    public ResponseEntity<RouteResponseDto> getRoute(
            @Valid @RequestBody RouteRequestDto request,
            Authentication authentication) {
        String role = extractUserRole(authentication);
        RouteResponseDto response = routeService.calculateRoute(request, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pois/nearest")
    public ResponseEntity<RouteResponseDto> getNearestPoi(
            @Valid @RequestBody NearestPoiRequestDto request,
            Authentication authentication) {
        String role = extractUserRole(authentication);
        RouteResponseDto response = routeService.findNearestPoi(request, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/routes/multi-stop")
    public ResponseEntity<RouteResponseDto> getMultiStopRoute(
            @Valid @RequestBody MultiStopRouteRequestDto request,
            Authentication authentication) {
        String role = extractUserRole(authentication);
        RouteResponseDto response = routeService.findMultiStopRoute(request, role);
        return ResponseEntity.ok(response);
    }

    private String extractUserRole(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return "ROLE_USER";
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
    }
}
