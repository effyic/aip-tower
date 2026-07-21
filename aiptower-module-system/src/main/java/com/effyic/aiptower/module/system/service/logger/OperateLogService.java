package com.effyic.aiptower.module.system.service.logger;

import com.effyic.aiptower.framework.common.pojo.PageResult;
import com.effyic.aiptower.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import com.effyic.aiptower.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.effyic.aiptower.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.effyic.aiptower.module.system.dal.dataobject.logger.OperateLogDO;

/**
 * 操作日志 Service 接口
 *
 * @author effyic
 */
public interface OperateLogService {

    /**
     * 记录操作日志
     *
     * @param createReqDTO 创建请求
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 获得操作日志
     *
     * @param id 编号
     * @return 操作日志
     */
    OperateLogDO getOperateLog(Long id);

    /**
     * 获得操作日志分页列表
     *
     * @param pageReqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO);

    /**
     * 获得操作日志分页列表
     *
     * @param pageReqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqVO);

}
