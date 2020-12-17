package cn.itcast.dtx.notifydemo.bank1.service;

import cn.itcast.dtx.notifydemo.bank1.entity.AccountPay;
import cn.itcast.dtx.notifydemo.bank1.model.AccountChangeEvent;

/**
 * @author: duhao
 * @date: 2020/12/16 14:55
 */
public interface AccountInfoService {
    // 更新账户金额
    public void updateAccountBalance(AccountChangeEvent accountChange);
    //主动查询
    public AccountPay queryPayResult(String tx_no);

}
