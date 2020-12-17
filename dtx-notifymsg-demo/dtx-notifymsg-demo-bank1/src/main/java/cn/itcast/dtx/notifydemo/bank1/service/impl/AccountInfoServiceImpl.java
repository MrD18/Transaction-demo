package cn.itcast.dtx.notifydemo.bank1.service.impl;

import cn.itcast.dtx.notifydemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.notifydemo.bank1.entity.AccountPay;
import cn.itcast.dtx.notifydemo.bank1.fegin.PayClient;
import cn.itcast.dtx.notifydemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.notifydemo.bank1.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: duhao
 * @date: 2020/12/16 14:56
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

    @Autowired
    private AccountInfoDao accountInfoDao;
    @Autowired
    private PayClient payClient;


    /**
     * 1、监听MQ，接收充值结果，根据充值结果完成账户金额修改。
     * 2、主动查询充值系统，根据充值结果完成账户金额修改。
     * @param accountChange
     */
    @Override
    public void updateAccountBalance(AccountChangeEvent accountChange) {
        // 进行幂等判断
        if (accountInfoDao.isExistTx(accountChange.getTxNo())>0){
         return;
        }

         // 更新金额
         accountInfoDao.updateAccountBalance(accountChange.getAccountNo(),accountChange.getAmount());
         // 插入事务控制,用于幂等判断
        accountInfoDao.addTx(accountChange.getTxNo());

    }
// 远程调用查询充值结果
    @Override
    public AccountPay queryPayResult(String tx_no) {
              //远程调用
        log.info("主动查询:远程调用开始,tx_no:{}",tx_no);
        AccountPay payresult = payClient.payresult(tx_no);
     if ("success".equals(payresult.getResult())){ // 如果查询成功, 则才会进行更新
         //更新账户金额
         AccountChangeEvent accountChangeEvent = new AccountChangeEvent();
         accountChangeEvent.setAccountNo(payresult.getAccountNo());
         accountChangeEvent.setAmount(payresult.getPayAmount());
         accountChangeEvent.setTxNo(payresult.getId());// 充值事务号
         updateAccountBalance(accountChangeEvent);
         log.info("主动查询:远程调用结束,tx_no:{},AccountNo:{},Amount:{}",accountChangeEvent.getTxNo(), accountChangeEvent.getAccountNo(),accountChangeEvent.getAmount());
      }

        return payresult;

    }
}
