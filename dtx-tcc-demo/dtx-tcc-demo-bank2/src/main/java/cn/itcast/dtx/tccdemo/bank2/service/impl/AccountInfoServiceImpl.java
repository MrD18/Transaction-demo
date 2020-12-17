package cn.itcast.dtx.tccdemo.bank2.service.impl;


import cn.itcast.dtx.tccdemo.bank2.dao.AccountInfoDao;
import cn.itcast.dtx.tccdemo.bank2.service.AccountInfoService;
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

       // 账户加钱
    @Override
    // 只要标记@Hmily就是try方法, 在注解中指定confirm  cancel两个方法的名字
    @Hmily(confirmMethod="commit",cancelMethod = "rollback")
    public void updateAccountBalance(String accountNo, Double amount) {
         // 获取事务id
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("bank2 try begin 开始执行...,xid:{}",transId);

    }
    //confirm方法
  /*  confirm幂等校验
      正式增加30元*/
  @Transactional
    public  void commit(String accountNo, Double amount){
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("bank2 confirm begin 开始执行...,xid:{}",transId);
        if (accountInfoDao.isExistConfirm(transId)>0){ //幂等性校验，已经执行过了，什么也不用做
            log.info("******** Bank2 已经执行过confirm... 无需再次confirm,xid:{} ",transId );
            return;
        }
     //正式增加金额
        accountInfoDao.addAccountBalance(accountNo,amount);
        //添加confirm日志
        accountInfoDao.addConfirm(transId);
      log.info("bank2 confirm end 结束....xid:{}",transId);
    }
   /* cancel幂等校验
     cancel空回滚处理
    增加可用余额30元*/

    //cancel方法
    public  void rollback(String accountNo, Double amount) {

        // 获取事务id
        String transId = HmilyTransactionContextLocal.getInstance().get().getTransId();
        log.info("bank2 cancel方法 begin 开始执行...,xid:{}",transId);
    }
}
