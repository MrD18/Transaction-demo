package cn.itcast.dtx.tccdemo.bank1.feign;

import org.springframework.stereotype.Component;

/**
 * @author: duhao
 * @date: 2020/12/14 18:14
 */
@Component
public class Bank2Fallback implements Bank2Client {
    @Override
    public String transfer(Double amount) {
        return "调用Bank2 接口失败";
    }
}






