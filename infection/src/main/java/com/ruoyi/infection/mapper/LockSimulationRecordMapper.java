package com.ruoyi.infection.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
public interface LockSimulationRecordMapper {
    List<Long> selectIdsByCity(@Param("city") String city,@Param("userId") String userId);
    String selectFilepathById(Long id);
    List<Long> selectIdByCity(@Param("city") String city,@Param("userId") String userId);
    String selectFilespathById(Long id);
    List<Long> selectMADDPGIdByCity(@Param("city") String city,@Param("userId") String userId);
    String selectMADDPGFilespathById(Long id);
    Integer getLatestSimulationId(@Param("city") String city,@Param("userId") String userId);
    String getFilePathById(Integer id);
}