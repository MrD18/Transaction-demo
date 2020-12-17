package cn.itcast.dtx.txmsgdemo.bank2.message;

import cn.itcast.dtx.txmsgdemo.bank2.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank2.service.AccountInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** MQ监听类
 * @author: duhao
 * @date: 2020/12/15 13:05
 */
@Component
@Slf4j
@RocketMQMessageListener(topic ="topic_txmsg",consumerGroup = "consumer_txmsg_group_bank2")
public class TxmsgConsumer implements RocketMQListener<String>{

   @Autowired
   private AccountInfoService accountInfoService;

    @Override
    public void onMessage(String s) {
          log.info("开始消费消息:{}",s);
          // 解析消息为对象
        // 解析MQ回传过来的消息
        final JSONObject jsonObject = JSON.parseObject(s);
        AccountChangeEvent accountChangeEvent =
                JSONObject.parseObject(jsonObject.getString("accountChange"),AccountChangeEvent.class);
        // 调用service增加李四账户的余额
        accountChangeEvent.setAccountNo("2");
        accountInfoService.addAccountInfoBalance(accountChangeEvent);





    }
}
