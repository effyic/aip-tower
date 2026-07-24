package com.effyic.aiptower.module.system.convert.tenant;

import com.effyic.aiptower.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 租户 Convert
 *
 * @author effyic
 */
@Mapper
public interface TenantConvert {

    TenantConvert INSTANCE = Mappers.getMapper(TenantConvert.class);

    default UserSaveReqVO convert02(String username, String password, String nickname, String mobile) {
        UserSaveReqVO reqVO = new UserSaveReqVO();
        reqVO.setUsername(username);
        reqVO.setPassword(password);
        reqVO.setNickname(nickname);
        reqVO.setMobile(mobile);
        return reqVO;
    }

}
