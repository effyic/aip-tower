package com.effyic.aiptower.module.product.convert.brand;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.module.product.controller.admin.brand.vo.ProductBrandCreateReqVO;
import com.effyic.aiptower.module.product.controller.admin.brand.vo.ProductBrandRespVO;
import com.effyic.aiptower.module.product.controller.admin.brand.vo.ProductBrandSimpleRespVO;
import com.effyic.aiptower.module.product.controller.admin.brand.vo.ProductBrandUpdateReqVO;
import com.effyic.aiptower.module.product.dal.dataobject.brand.ProductBrandDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 品牌 Convert
 *
 * @author effyic
 */
@Mapper
public interface ProductBrandConvert {

    ProductBrandConvert INSTANCE = Mappers.getMapper(ProductBrandConvert.class);

    ProductBrandDO convert(ProductBrandCreateReqVO bean);

    ProductBrandDO convert(ProductBrandUpdateReqVO bean);

    ProductBrandRespVO convert(ProductBrandDO bean);

    List<ProductBrandSimpleRespVO> convertList1(List<ProductBrandDO> list);

    List<ProductBrandRespVO> convertList(List<ProductBrandDO> list);

    PageResult<ProductBrandRespVO> convertPage(PageResult<ProductBrandDO> page);

}
