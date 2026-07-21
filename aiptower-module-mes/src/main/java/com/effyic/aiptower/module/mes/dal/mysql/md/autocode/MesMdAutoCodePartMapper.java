package com.effyic.aiptower.module.mes.dal.mysql.md.autocode;

import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.mes.dal.dataobject.md.autocode.MesMdAutoCodePartDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MES 编码规则组成 Mapper
 *
 * @author effyic
 */
@Mapper
public interface MesMdAutoCodePartMapper extends BaseMapperX<MesMdAutoCodePartDO> {

    default List<MesMdAutoCodePartDO> selectListByRuleId(Long ruleId) {
        return selectList(new LambdaQueryWrapperX<MesMdAutoCodePartDO>()
                .eq(MesMdAutoCodePartDO::getRuleId, ruleId)
                .orderByAsc(MesMdAutoCodePartDO::getSort));
    }

    default void deleteByRuleId(Long ruleId) {
        delete(MesMdAutoCodePartDO::getRuleId, ruleId);
    }

}
