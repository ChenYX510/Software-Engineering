package com.ruoyi.infection.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
public interface LockSimulationRecordMapper {
    List<Long> selectIdsByCity(String city);
    String selectFilepathById(Long id);
    List<Long> selectIdByCity(String city);
    String selectFilespathById(Long id);
}