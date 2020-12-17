package cn.itcast.dtx.notifymsg.pay.controller;

import cn.itcast.dtx.notifymsg.pay.entity.AccountPay;
import cn.itcast.dtx.notifymsg.pay.service.AccountPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author: duhao
 * @date: 2020/12/16 14:50
 */
@RestController
public class AccountPayController {

    @Autowired
    private AccountPayService accountPayService;
@GetMapping("/paydo")
    public AccountPay pay(AccountPay accountPay) {
    // 事务号
    String txNo = UUID.randomUUID().toString();
    accountPay.setId(txNo);
    return accountPayService.insertAccountPay(accountPay);

}
    //查询充值结果
    @GetMapping("/payresult/{txNo}")
    public AccountPay payresult(@PathVariable("txNo") String txNo){
        return accountPayService.getAccountPay(txNo);

    }
}
