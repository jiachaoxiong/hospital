package com.hospital.payment.service.impl;
import cn.hutool.core.lang.UUID;
import com.hospital.common.BusinessException;
import com.hospital.common.R;
import com.hospital.payment.entity.PaymentRecord;
import com.hospital.payment.mapper.PaymentRecordMapper;
import com.hospital.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRecordMapper paymentRecordMapper;

    @Override
    public R<Map<String, String>> createPayment(Long orderId, Double amount) {
        String tradeNo = "WX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderId);
        record.setPayMethod("WECHAT");
        record.setTradeNo(tradeNo);
        record.setAmount(amount);
        record.setStatus("PENDING");
        paymentRecordMapper.insert(record);
        Map<String, String> result = new HashMap<>();
        result.put("tradeNo", tradeNo);
        result.put("qrcodeUrl", "/payment/qrcode?orderId=" + orderId + "&amount=" + amount + "&tradeNo=" + tradeNo);
        log.info("【模拟支付】生成支付订单: tradeNo={}", tradeNo);
        return R.ok(result);
    }

    @Override
    public void paymentCallback(Long orderId, String tradeNo) {
        PaymentRecord record = paymentRecordMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getOrderId, orderId)
                        .eq(PaymentRecord::getTradeNo, tradeNo));
        if (record == null) throw new BusinessException("支付记录不存在");
        record.setStatus("SUCCESS");
        paymentRecordMapper.updateById(record);
        log.info("【模拟支付】支付成功: orderId={}, tradeNo={}", orderId, tradeNo);
    }
}
