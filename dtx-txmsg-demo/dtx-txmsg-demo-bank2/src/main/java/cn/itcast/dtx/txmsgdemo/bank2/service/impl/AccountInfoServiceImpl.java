package cn.itcast.dtx.txmsgdemo.bank2.service.impl;

import cn.itcast.dtx.txmsgdemo.bank2.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank2.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank2.service.AccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: duhao
 * @date: 2020/12/15 12:33
 */
@Slf4j
@Service
public class AccountInfoServiceImpl implements AccountInfoService {
   @Autowired
   private AccountInfoDao accountInfoDao;

    /**
     * 消费消息，更新本地事务，添加金额
     *
     * @param accountChangeEvent
     */
    @Override
    @Transactional
    public void addAccountInfoBalance(AccountChangeEvent accountChangeEvent) {
     // 幂等性校验
        if (accountInfoDao.isExistTx(accountChangeEvent.getTxNo())<=0){
            //第一次,那就进行更新
            accountInfoDao.updateAccountBalance(accountChangeEvent.getAccountNo(),accountChangeEvent.getAmount());
            //添加到记录中
            accountInfoDao.addTx(accountChangeEvent.getTxNo());
            log.info("更新本地事务执行成功，本次事务号: {}", accountChangeEvent.getTxNo());
        }
        else {
            log.info("更新本地事务执行失败，本次事务号: {}", accountChangeEvent.getTxNo());
        }
    }
}
