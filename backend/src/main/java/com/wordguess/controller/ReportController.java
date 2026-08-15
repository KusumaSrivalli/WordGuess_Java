package com.wordguess.controller;

import com.wordguess.dto.*;
import com.wordguess.model.User;
import com.wordguess.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/overview")
    public ResponseEntity<PlatformOverviewDto> getPlatformOverview() {
        return ResponseEntity.ok(reportService.getPlatformOverview());
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyReportDto> getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        DailyReportDto report = reportService.getDailyReport(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/range")
    public ResponseEntity<List<DateRangeReportRowDto>> getDateRangeReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getDateRangeReport(startDate, endDate));
    }

    @GetMapping("/users-directory")
    public ResponseEntity<List<UserDetailAdminDto>> getAdminUsersDirectory() {
        return ResponseEntity.ok(reportService.getAdminUsersDirectory());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        try {
            String username = payload.get("username");
            String role = payload.get("role");
            User updated = reportService.updateUser(id, username, role);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        try {
            reportService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReport(@PathVariable String userId) {
        try {
            List<UserReportDto> report = reportService.getUserReport(userId);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/consistency-heatmap")
    public ResponseEntity<?> getUserConsistencyHeatmap(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(reportService.getUserConsistencyHeatmap(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/letter-heatmap")
    public ResponseEntity<?> getUserLetterHeatmap(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(reportService.getUserLetterHeatmap(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/players")
    public ResponseEntity<List<User>> getAllPlayers() {
        return ResponseEntity.ok(reportService.getAllPlayers());
    }
}
