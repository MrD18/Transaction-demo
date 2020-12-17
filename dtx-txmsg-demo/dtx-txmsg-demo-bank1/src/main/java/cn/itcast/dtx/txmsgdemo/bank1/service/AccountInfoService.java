package cn.itcast.dtx.txmsgdemo.bank1.service;

import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;

/**
 * @author: duhao
 * @date: 2020/12/15 11:13
 */
public interface AccountInfoService {
   // 发送消息
    public void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent);
    //更新账户余额,完成本地事务
    public void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent);

}
