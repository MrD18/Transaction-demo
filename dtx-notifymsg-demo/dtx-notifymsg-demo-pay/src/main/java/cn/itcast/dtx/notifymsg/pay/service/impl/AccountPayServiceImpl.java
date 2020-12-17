package cn.itcast.dtx.notifymsg.pay.service.impl;

import cn.itcast.dtx.notifymsg.pay.dao.AccountPayDao;
import cn.itcast.dtx.notifymsg.pay.entity.AccountPay;
import cn.itcast.dtx.notifymsg.pay.service.AccountPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

/**
 * @author: duhao
 * @date: 2020/12/16 14:43
 */
@Slf4j
@Service
public class AccountPayServiceImpl implements AccountPayService {

    @Autowired
    private AccountPayDao accountPayDao;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 1、充值接口
     * 2、充值完成要通知
     * 3、充值结果查询接口
     * @param accountPay
     * @return
     */
    //插入充值记录
    @Override
    public AccountPay insertAccountPay(AccountPay accountPay) {
        // 1.进行充值
        int success = accountPayDao.insertAccountPay(accountPay.getId(), accountPay.getAccountNo(), accountPay.getPayAmount(), "success");

        if (success>0){
              // 2.发送通知,使用普通消息发送通知
            accountPay.setResult("success");
            /**
             * "topic_notifymsg"   主题
             * accountPay   消息
             */
         rocketMQTemplate.convertAndSend("topic_notifymsg",accountPay);
       //   rocketMQTemplate.send("topic_notifymsg", (Message<?>) accountPay);
            return accountPay;
        }
        return null;
    }
      //3.充值结果查询接口
    @Override
    public AccountPay getAccountPay(String txNo) {

        AccountPay accountPay = accountPayDao.findByIdTxNo(txNo);
        return accountPay;
    }
}
