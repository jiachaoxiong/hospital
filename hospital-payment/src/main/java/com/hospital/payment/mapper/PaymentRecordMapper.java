package com.hospital.payment.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.payment.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
}
