package com.siemens.internship.controller;

import com.siemens.internship.dto.request.DelayRequest;
import com.siemens.internship.dto.request.ScheduleRequest;
import com.siemens.internship.dto.response.DelayResponse;
import com.siemens.internship.dto.response.ScheduleResponse;
import com.siemens.internship.service.AdminScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/schedules")
public class AdminScheduleController {

    private final AdminScheduleService adminScheduleService;

    public AdminScheduleController(AdminScheduleService adminScheduleService) {
        this.adminScheduleService = adminScheduleService;
    }

    @GetMapping
    public List<ScheduleResponse> getAllSchedules() {
        return adminScheduleService.getAllSchedules();
    }

    @GetMapping("/{scheduleId}")
    public ScheduleResponse getScheduleById(@PathVariable Long scheduleId) {
        return adminScheduleService.getScheduleById(scheduleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse createSchedule(@Valid @RequestBody ScheduleRequest request) {
        return adminScheduleService.createSchedule(request);
    }

    @PutMapping("/{scheduleId}")
    public ScheduleResponse updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody ScheduleRequest request
    ) {
        return adminScheduleService.updateSchedule(scheduleId, request);
    }

    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@PathVariable Long scheduleId) {
        adminScheduleService.deleteSchedule(scheduleId);
    }

    @PostMapping("/{scheduleId}/delay")
    public DelayResponse reportDelay(
            @PathVariable Long scheduleId,
            @Valid @RequestBody DelayRequest request
    ) {
        return adminScheduleService.reportDelay(scheduleId, request);
    }
}