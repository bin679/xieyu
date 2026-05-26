package com.labmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labmanager.common.Result;
import com.labmanager.entity.Equipment;
import com.labmanager.entity.Lab;
import com.labmanager.service.EquipmentService;
import com.labmanager.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private LabService labService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) Long labId,
                          @RequestParam(required = false) String status) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Equipment::getName, keyword).or().like(Equipment::getModel, keyword));
        }
        if (labId != null) {
            wrapper.eq(Equipment::getLabId, labId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Equipment::getStatus, status);
        }
        wrapper.orderByDesc(Equipment::getCreateTime);
        Page<Equipment> page = equipmentService.page(new Page<>(current, size), wrapper);

        List<Lab> labs = labService.list();
        Map<Long, String> labMap = labs.stream().collect(Collectors.toMap(Lab::getId, Lab::getName));

        page.getRecords().forEach(e -> {
            e.setLabName(labMap.get(e.getLabId()));
        });
        return Result.success(page);
    }

    @GetMapping("/all")
    public Result<List<Equipment>> all() {
        List<Equipment> list = equipmentService.listWithLab();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable Long id) {
        return Result.success(equipmentService.getById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Equipment equipment) {
        equipment.setId(null);
        equipmentService.save(equipment);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Equipment equipment) {
        equipment.setId(id);
        equipmentService.updateById(equipment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        equipmentService.removeById(id);
        return Result.success();
    }
}
