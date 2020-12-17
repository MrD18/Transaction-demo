package cn.itcast.dtx.notifymsg.pay.service;

import cn.itcast.dtx.notifymsg.pay.entity.AccountPay;

/**充值接口
 * @author: duhao
 * @date: 2020/12/16 14:40
 */
public interface AccountPayService {

    //充值接口
    public AccountPay insertAccountPay(AccountPay accountPay);

    //查询接口
    public AccountPay getAccountPay(String txNo);

}
