package cn.itcast.dtx.txmsgdemo.bank2.service;

import cn.itcast.dtx.txmsgdemo.bank2.model.AccountChangeEvent;

/**
 * @author: duhao
 * @date: 2020/12/15 12:33
 */
public interface AccountInfoService {
    //增加金额
    public void addAccountInfoBalance(AccountChangeEvent accountChangeEvent);
}
