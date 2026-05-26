package com.labmanager.controller;

import com.labmanager.common.Result;
import com.labmanager.entity.BorrowRecord;
import com.labmanager.service.BorrowRecordService;
import com.labmanager.service.EquipmentService;
import com.labmanager.service.LabService;
import com.labmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private LabService labService;

    @Autowired
    private UserService userService;

    @Autowired
    private BorrowRecordService borrowRecordService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("labCount", labService.count());
        data.put("equipmentCount", equipmentService.count());
        data.put("userCount", userService.count());
        data.put("borrowingCount", borrowRecordService.count(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getStatus, "borrowing")));
        data.put("overdueCount", borrowRecordService.listOverdue().size());
        data.put("equipmentByStatus", equipmentService.countByStatus());
        data.put("equipmentByLab", equipmentService.countByLab());
        return Result.success(data);
    }

    @GetMapping("/overdue")
    public Result<List<BorrowRecord>> overdue() {
        return Result.success(borrowRecordService.listOverdue());
    }
}