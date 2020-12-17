package cn.itcast.dtx.tccdemo.bank1.service.impl;

import cn.itcast.dtx.tccdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.tccdemo.bank1.feign.Bank2Client;
import cn.itcast.dtx.tccdemo.bank1.service.AccountInfoService;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.Hmily;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: duhao
 * @date: 2020/12/14 17:04
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {

       @Autowired
       private AccountInfoDao accountInfoDao;
       @Autowired
       private Bank2Client bank2Client;
       // 账户扣款


  /*  try：
       try幂等校验
       try悬挂处理
        检查余额是否够30元
       扣减30元*/
    @Override
    // 只要标记@Hmily就是try方法, 在注解中指定confirm  cancel两个方法的名字
    @Hmily(confirmMethod="commit",cancelMethod = "rollback")
    @Transactional
    public void updateAccountBalance(String accountNo, Double amount) {

         // 获取事务id
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();

        log.info("bank1 try start 开始执行....xid:{}",transId);
        //幂等判断,判断local_try_log表中是否有try日志记录,如果有就不执行
          if ( accountInfoDao.isExistTry(transId)>0){
              log.info("bank1 try 已经执行,无序重复执行,xid:{}",transId);
               return;
          }
          //try悬挂处理,如果cancel,confirm有一个已经执行了,try不在执行
        if (accountInfoDao.isExistCancel(transId)>0||accountInfoDao.isExistConfirm(transId)>0){
            log.info("bank1 try已经处于悬挂 cancel或confirm已经执行, 不允许执行try,xid:{}",transId);
            return;
        }
        //扣减金额
        if (accountInfoDao.subtractAccountBalance(accountNo,amount)<=0){
            //扣减失败
            throw  new RuntimeException("bank1 try 扣减金额失败,xid:{}"+transId);
        }
        //插入try执行记录,用于幂等性判断
        accountInfoDao.addTry(transId);
        //远程调用李四,转账
        try {
            bank2Client.transfer(amount);
        } catch (Exception e) {
            throw  new RuntimeException("bank1 远程调用李四bank2失败:{}"+transId);
        }

        //人为的制造异常
        if(amount==2){
            throw  new RuntimeException("人为制造的异常:{}"+transId);
        }
       log.info("bank1 try end 结束....xid:{}",transId);
    }
    //confirm方法
    public  void commit(String accountNo, Double amount){
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("bank1 confirm begin 开始执行...,xid:{}",transId);
    }
   /* cancel幂等校验
     cancel空回滚处理
    增加可用余额30元*/

    //cancel方法
    @Transactional
    public  void rollback(String accountNo, Double amount){

        // 获取事务id
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        //cancel幂等校验
       if (accountInfoDao.isExistCancel(transId)>0){
           log.info("bank1 cancel 已经执行,无序重复执行,xid:{}",transId);
           return;
       }
       //cancel空回滚处理 如果try没有执行,那么cancel 就不能执行
        if(accountInfoDao.isExistTry(transId)==0){
            log.info("bank1 空回滚,try没有执行,不允许cancel执行,xid:{}",transId);
            return;
        }
        //增加可用余额
        accountInfoDao.addAccountBalance(accountNo,amount);
        //插入一条cancel的执行记录
        accountInfoDao.addCancel(transId);
        log.info("bank1 rollback end 结束...,xid:{}",transId);
    }
}
