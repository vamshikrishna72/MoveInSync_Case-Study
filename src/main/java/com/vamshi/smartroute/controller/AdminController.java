package com.vamshi.smartroute.controller;

import com.vamshi.smartroute.dto.EdgeDto;
import com.vamshi.smartroute.dto.NodeDto;
import com.vamshi.smartroute.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/nodes")
    public ResponseEntity<NodeDto> createNode(@Valid @RequestBody NodeDto dto) {
        NodeDto created = adminService.createNode(dto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/edges")
    public ResponseEntity<EdgeDto> createEdge(@Valid @RequestBody EdgeDto dto) {
        EdgeDto created = adminService.createEdge(dto);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/edges/{id}/congestion")
    public ResponseEntity<String> updateCongestion(
            @PathVariable UUID id,
            @RequestParam double multiplier) {
        adminService.updateEdgeCongestion(id, multiplier);
        return ResponseEntity.ok("Congestion updated successfully for edge " + id);
    }
}
