package com.labmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labmanager.common.Result;
import com.labmanager.entity.BorrowRecord;
import com.labmanager.entity.Equipment;
import com.labmanager.mapper.EquipmentMapper;
import com.labmanager.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @GetMapping
    public Result<Page<BorrowRecord>> list(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BorrowRecord::getCreateTime);
        return Result.success(borrowRecordService.page(new Page<>(current, size), wrapper));
    }

    @GetMapping("/details")
    public Result<List<BorrowRecord>> listWithDetails() {
        return Result.success(borrowRecordService.listWithDetails());
    }

    @GetMapping("/overdue")
    public Result<List<BorrowRecord>> listOverdue() {
        return Result.success(borrowRecordService.listOverdue());
    }

    @GetMapping("/{id}")
    public Result<BorrowRecord> getById(@PathVariable Long id) {
        return Result.success(borrowRecordService.getById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody BorrowRecord record) {
        Equipment equipment = equipmentMapper.selectById(record.getEquipmentId());
        if (equipment == null) {
            return Result.error(400, "设备不存在");
        }
        if (!"available".equals(equipment.getStatus())) {
            return Result.error(400, "设备当前不可借用");
        }
        record.setId(null);
        record.setBorrowTime(LocalDateTime.now());
        record.setStatus("borrowing");
        borrowRecordService.save(record);

        equipment.setStatus("borrowed");
        equipmentMapper.updateById(equipment);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody BorrowRecord record) {
        record.setId(id);
        borrowRecordService.updateById(record);
        return Result.success();
    }

    @PutMapping("/{id}/return")
    public Result<?> returnEquipment(@PathVariable Long id) {
        borrowRecordService.returnEquipment(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        BorrowRecord record = borrowRecordService.getById(id);
        if (record != null) {
            Equipment equipment = equipmentMapper.selectById(record.getEquipmentId());
            if (equipment != null && "borrowed".equals(equipment.getStatus())) {
                equipment.setStatus("available");
                equipmentMapper.updateById(equipment);
            }
        }
        borrowRecordService.removeById(id);
        return Result.success();
    }
}
