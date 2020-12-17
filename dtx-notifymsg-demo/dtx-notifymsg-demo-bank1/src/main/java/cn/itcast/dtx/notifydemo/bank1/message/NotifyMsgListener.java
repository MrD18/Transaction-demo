package cn.itcast.dtx.notifydemo.bank1.message;

import cn.itcast.dtx.notifydemo.bank1.entity.AccountPay;
import cn.itcast.dtx.notifydemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.notifydemo.bank1.service.AccountInfoService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: duhao
 * @date: 2020/12/16 16:16
 */
@Component
@Slf4j
// 监听的主题   和 消费的主题
@RocketMQMessageListener(topic="topic_notifymsg",consumerGroup="consumer_group_notifymsg_bank1")
public class NotifyMsgListener implements RocketMQListener<AccountPay>{
    @Autowired
    private AccountInfoService accountInfoService;
    // 接受消息
    @Override
    public void onMessage(AccountPay accountPay) {
        log.info("接收到消息：{}", JSON.toJSONString(accountPay));
      //监听的消息是成功的, 那就更新
        if ("success".equals(accountPay.getResult())){
            // 更新账户
            AccountChangeEvent accountChangeEvent = new AccountChangeEvent();
            accountChangeEvent.setAccountNo(accountPay.getAccountNo());
            accountChangeEvent.setAmount(accountPay.getPayAmount());
            accountChangeEvent.setTxNo(accountPay.getId());// 充值事务号
            accountInfoService.updateAccountBalance(accountChangeEvent);
        }
        log.info("处理消息完成：{}", JSON.toJSONString(accountPay));
    }
}
