package com.effyic.aiptower.module.mes.dal.mysql.md.workstation;

import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.module.mes.dal.dataobject.md.workstation.MesMdWorkstationMachineDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 设备资源 Mapper
 *
 * @author effyic
 */
@Mapper
public interface MesMdWorkstationMachineMapper extends BaseMapperX<MesMdWorkstationMachineDO> {

    default List<MesMdWorkstationMachineDO> selectListByWorkstationId(Long workstationId) {
        return selectList(MesMdWorkstationMachineDO::getWorkstationId, workstationId);
    }

    default MesMdWorkstationMachineDO selectByMachineryId(Long machineryId) {
        return selectOne(MesMdWorkstationMachineDO::getMachineryId, machineryId);
    }

    default void deleteByWorkstationId(Long workstationId) {
        delete(MesMdWorkstationMachineDO::getWorkstationId, workstationId);
    }

}
