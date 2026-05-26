package com.labmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labmanager.common.Result;
import com.labmanager.entity.Equipment;
import com.labmanager.entity.RepairRecord;
import com.labmanager.mapper.EquipmentMapper;
import com.labmanager.service.RepairRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/repairs")
public class RepairRecordController {

    @Autowired
    private RepairRecordService repairRecordService;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @GetMapping
    public Result<Page<RepairRecord>> list(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<RepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RepairRecord::getCreateTime);
        return Result.success(repairRecordService.page(new Page<>(current, size), wrapper));
    }

    @GetMapping("/details")
    public Result<List<RepairRecord>> listWithDetails() {
        return Result.success(repairRecordService.listWithDetails());
    }

    @GetMapping("/{id}")
    public Result<RepairRecord> getById(@PathVariable Long id) {
        return Result.success(repairRecordService.getById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody RepairRecord record) {
        record.setId(null);
        record.setStatus("pending");
        repairRecordService.save(record);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody RepairRecord record) {
        record.setId(id);
        RepairRecord existing = repairRecordService.getById(id);

        // 维修开始
        if ("repairing".equals(record.getStatus()) && !"repairing".equals(existing.getStatus())) {
            record.setRepairTime(LocalDateTime.now());
            Equipment equipment = equipmentMapper.selectById(existing.getEquipmentId());
            if (equipment != null) {
                equipment.setStatus("maintenance");
                equipmentMapper.updateById(equipment);
            }
        }
        // 维修完成
        if ("completed".equals(record.getStatus()) && !"completed".equals(existing.getStatus())) {
            record.setCompleteTime(LocalDateTime.now());
            Equipment equipment = equipmentMapper.selectById(existing.getEquipmentId());
            if (equipment != null) {
                equipment.setStatus("available");
                equipmentMapper.updateById(equipment);
            }
        }
        repairRecordService.updateById(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        repairRecordService.removeById(id);
        return Result.success();
    }
}
