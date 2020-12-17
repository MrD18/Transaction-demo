package cn.itcast.dtx.tccdemo.bank2.controller;

import cn.itcast.dtx.tccdemo.bank2.service.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: duhao
 * @date: 2020/12/14 18:29
 */
@RestController
public class controller {
    @Autowired
    AccountInfoService accountInfoService;
    @RequestMapping("/transfer")
    public  String transfer(@RequestParam("amount") Double amount) {
        accountInfoService.updateAccountBalance("2", amount);
    return "zhangsan调用李四";
    }
}
