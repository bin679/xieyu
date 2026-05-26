package com.labmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.labmanager.common.Result;
import com.labmanager.entity.Lab;
import com.labmanager.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labs")
public class LabController {

    @Autowired
    private LabService labService;

    @GetMapping
    public Result<Page<Lab>> list(@RequestParam(defaultValue = "1") Integer current,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Lab> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Lab::getName, keyword).or().like(Lab::getLocation, keyword);
        }
        wrapper.orderByDesc(Lab::getCreateTime);
        return Result.success(labService.page(new Page<>(current, size), wrapper));
    }

    @GetMapping("/all")
    public Result<List<Lab>> all() {
        return Result.success(labService.list());
    }

    @GetMapping("/{id}")
    public Result<Lab> getById(@PathVariable Long id) {
        return Result.success(labService.getById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Lab lab) {
        lab.setId(null);
        labService.save(lab);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Lab lab) {
        lab.setId(id);
        labService.updateById(lab);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        labService.removeById(id);
        return Result.success();
    }
}
