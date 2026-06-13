package com.hospital.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper — 基于 MyBatis-Plus 提供订单表的 CRUD 操作
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
