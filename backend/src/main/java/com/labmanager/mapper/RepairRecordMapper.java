package com.labmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.labmanager.entity.RepairRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RepairRecordMapper extends BaseMapper<RepairRecord> {

    @Select("SELECT rr.*, e.name AS equipment_name, u.real_name AS user_name " +
            "FROM repair_record rr " +
            "LEFT JOIN equipment e ON rr.equipment_id = e.id " +
            "LEFT JOIN user u ON rr.user_id = u.id " +
            "ORDER BY rr.create_time DESC")
    List<RepairRecord> selectAllWithDetails();
}
