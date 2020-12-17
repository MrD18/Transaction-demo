package cn.itcast.dtx.tccdemo.bank1.controller;

import cn.itcast.dtx.tccdemo.bank1.service.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: duhao
 * @date: 2020/12/14 18:33
 */
@RestController
public class Bank1Controller {
    @Autowired
    AccountInfoService accountInfoService;
    @RequestMapping("/transfer")
    public String test(@RequestParam("amount") Double amount) {
        accountInfoService.updateAccountBalance("1", amount);
        return "cn/itcast/dtx/tccdemo/bank1" + amount;
    }
}
