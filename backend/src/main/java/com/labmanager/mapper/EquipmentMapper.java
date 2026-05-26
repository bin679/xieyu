package com.labmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labmanager.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {

    @Select("SELECT e.*, l.name AS lab_name FROM equipment e LEFT JOIN lab l ON e.lab_id = l.id")
    List<Equipment> selectAllWithLab();

    @Select("SELECT status, COUNT(*) AS count FROM equipment GROUP BY status")
    List<Map<String, Object>> countByStatus();

    @Select("SELECT l.name AS lab_name, COUNT(e.id) AS count FROM lab l LEFT JOIN equipment e ON l.id = e.lab_id GROUP BY l.id, l.name")
    List<Map<String, Object>> countByLab();
}
