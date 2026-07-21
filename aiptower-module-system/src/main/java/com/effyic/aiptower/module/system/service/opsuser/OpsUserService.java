package com.effyic.aiptower.module.system.service.opsuser;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserPageReqVO;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserRespVO;
import com.effyic.aiptower.module.system.controller.admin.opsuser.vo.OpsUserSaveReqVO;
import com.effyic.aiptower.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;

import java.util.List;

/**
 * 运营用户 Service（菜单树授权 + 每用户影子角色）
 */
public interface OpsUserService {

    PageResult<OpsUserRespVO> getOpsUserPage(OpsUserPageReqVO pageReqVO);

    OpsUserRespVO getOpsUser(Long id);

    Long createOpsUser(OpsUserSaveReqVO createReqVO);

    void updateOpsUser(OpsUserSaveReqVO updateReqVO);

    void updateOpsUserStatus(Long id, Integer status);

    void deleteOpsUser(Long id);

    /**
     * 可授权菜单精简列表（供前端勾选树）
     */
    List<MenuSimpleRespVO> getOpsMenuSimpleList();

}
