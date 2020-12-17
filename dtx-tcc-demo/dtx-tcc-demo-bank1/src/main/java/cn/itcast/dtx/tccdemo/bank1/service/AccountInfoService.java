package cn.itcast.dtx.tccdemo.bank1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author: duhao
 * @date: 2020/12/14 17:02
 */
public interface AccountInfoService {


    // 张三扣钱
    public void updateAccountBalance(String accountNo, Double amount);
}
