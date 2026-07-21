package com.effyic.aiptower.module.product.convert.spu;

import com.effyic.aiptower.framework.common.util.collection.CollectionUtils;
import com.effyic.aiptower.framework.common.util.object.BeanUtils;
import com.effyic.aiptower.module.product.controller.admin.spu.vo.ProductSkuRespVO;
import com.effyic.aiptower.module.product.controller.admin.spu.vo.ProductSpuPageReqVO;
import com.effyic.aiptower.module.product.controller.admin.spu.vo.ProductSpuRespVO;
import com.effyic.aiptower.module.product.controller.app.spu.vo.AppProductSpuPageReqVO;
import com.effyic.aiptower.module.product.dal.dataobject.sku.ProductSkuDO;
import com.effyic.aiptower.module.product.dal.dataobject.spu.ProductSpuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static com.effyic.aiptower.framework.common.util.collection.CollectionUtils.convertMultiMap;

/**
 * 商品 SPU Convert
 *
 * @author effyic
 */
@Mapper
public interface ProductSpuConvert {

    ProductSpuConvert INSTANCE = Mappers.getMapper(ProductSpuConvert.class);

    ProductSpuPageReqVO convert(AppProductSpuPageReqVO bean);

    default ProductSpuRespVO convert(ProductSpuDO spu, List<ProductSkuDO> skus) {
        ProductSpuRespVO spuVO = BeanUtils.toBean(spu, ProductSpuRespVO.class);
        spuVO.setSkus(BeanUtils.toBean(skus, ProductSkuRespVO.class));
        return spuVO;
    }

    default List<ProductSpuRespVO> convertForSpuDetailRespListVO(List<ProductSpuDO> spus, List<ProductSkuDO> skus) {
        Map<Long, List<ProductSkuDO>> skuMultiMap = convertMultiMap(skus, ProductSkuDO::getSpuId);
        return CollectionUtils.convertList(spus, spu -> convert(spu, skuMultiMap.get(spu.getId())));
    }

}
