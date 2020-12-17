package cn.itcast.dtx.txmsgdemo.bank1.service.impl;

import cn.itcast.dtx.txmsgdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank1.service.AccountInfoService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: duhao
 * @date: 2020/12/15 11:15
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {
    @Autowired
    private AccountInfoDao accountInfoDao;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * producer向MQ Server发送消息
     *
     * @param accountChangeEvent
     */
    @Override
    public void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {
        //封装消息---将accountChangeEvent转化为消息
        JSONObject jsonObject = new JSONObject();
         jsonObject.put("accountChange",accountChangeEvent);//封装成 k-v 形式
        // spring框架的Message
        Message<String> message = MessageBuilder.withPayload(jsonObject.toJSONString()).build();

        /**
         * String txProducerGroup 生产组
         * String destination   topic 主题
         * Message<?> message  消息内容
         * Object arg   参数
         */
        // 发送一条事务消息
           rocketMQTemplate.sendMessageInTransaction("producer_group_txmsg_bank1", "topic_txmsg", message,
                   null);
       // log.info("send transcation message body={},result={}",message.getPayload(),sendResult.getSendStatus());

    }

    /**
     * *producer发送消息完成后接收到MQ Server的回应即开始执行本地事务
     * @param accountChangeEvent
     */
    @Override
    @Transactional
    public void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {
        log.info("开始更新本地事务，事务号：{}",accountChangeEvent.getTxNo());
        //首先进行幂等校验  >0  不执行
      if ( accountInfoDao.isExistTx(accountChangeEvent.getTxNo())>0){
           return;
      }
      // 扣减金额
         accountInfoDao.updateAccountBalance(accountChangeEvent.getAccountNo(),accountChangeEvent.getAmount()*-1);
      // 添加事务日志
         accountInfoDao.addTx(accountChangeEvent.getTxNo());
        log.info("结束更新本地事务，事务号：{}",accountChangeEvent.getTxNo());

}
    }

