package com.effyic.aiptower.module.wms.dal.mysql.md.item;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.mybatis.core.mapper.BaseMapperX;
import com.effyic.aiptower.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.effyic.aiptower.module.wms.controller.admin.md.item.vo.brand.WmsItemBrandPageReqVO;
import com.effyic.aiptower.module.wms.dal.dataobject.md.item.WmsItemBrandDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * WMS 商品品牌 Mapper
 *
 * @author effyic
 */
@Mapper
public interface WmsItemBrandMapper extends BaseMapperX<WmsItemBrandDO> {

    default PageResult<WmsItemBrandDO> selectPage(WmsItemBrandPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WmsItemBrandDO>()
                .likeIfPresent(WmsItemBrandDO::getCode, reqVO.getCode())
                .likeIfPresent(WmsItemBrandDO::getName, reqVO.getName())
                .orderByDesc(WmsItemBrandDO::getId));
    }

    default WmsItemBrandDO selectByCode(String code) {
        return selectOne(WmsItemBrandDO::getCode, code);
    }

    default WmsItemBrandDO selectByName(String name) {
        return selectOne(WmsItemBrandDO::getName, name);
    }

}
