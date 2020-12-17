package cn.itcast.dtx.txmsgdemo.bank1.message;

import cn.itcast.dtx.txmsgdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank1.entity.AccountInfo;
import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank1.service.AccountInfoService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 监听消息的
 * 本地事务提交
 * 事务的回查
 * @author: duhao
 * @date: 2020/12/15 11:56
 */
@Component
@Slf4j
@RocketMQTransactionListener(txProducerGroup = "producer_group_txmsg_bank1") // 监听生产组
public class ProducerTxmsgListener implements RocketMQLocalTransactionListener {

    @Autowired
    private AccountInfoDao accountInfoDao;
    @Autowired
    private AccountInfoService accountInfoService;

    // 消息发送成功回调此方法,当消息发送给mq成功后,此方法被回调
    @Override
    @Transactional
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        // 解析MQ回传过来的消息,转成AccountChangeEvent
        String jsonString = new String((byte[]) message.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //将accountChangeEvent(json) 转成AccountChangeEvent
        AccountChangeEvent accountChangeEvent =
                JSONObject.parseObject(jsonObject.getString("accountChange"), AccountChangeEvent.class);
        try {
            // 执行本地事务,扣除金额
            accountInfoService.doUpdateAccountBalance(accountChangeEvent);
            //当返回RocketMQLocalTransactionState.COMMIT,自动向mq发送commit消息,mq将消息的状态改为可消费
            return RocketMQLocalTransactionState.COMMIT;

        } catch (Exception e) {
            //执行失败
            log.error("事务执行失败,回滚", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    // 事务状态的回查,查询是否扣减金额
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        // 解析MQ回传过来的消息
        String jsonString = new String((byte[]) message.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        AccountChangeEvent accountChangeEvent =
                JSONObject.parseObject(jsonObject.getString("accountChange"), AccountChangeEvent.class);
       //事务id
        String txNo = accountChangeEvent.getTxNo();
        int isexistTx = accountInfoDao.isExistTx(txNo);
        log.info("回查事务，事务号: {} 结果: {}", accountChangeEvent.getTxNo(), isexistTx);
        if (isexistTx > 0) {
           return RocketMQLocalTransactionState.COMMIT;
        } else {
           return RocketMQLocalTransactionState.UNKNOWN;
        }
    }
}