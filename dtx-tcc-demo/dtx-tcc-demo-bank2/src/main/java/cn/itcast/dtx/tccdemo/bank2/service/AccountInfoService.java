package cn.itcast.dtx.tccdemo.bank2.service;

/**
 * @author: duhao
 * @date: 2020/12/14 17:02
 */
public interface AccountInfoService {


    // 张三扣钱
    public void updateAccountBalance(String accountNo, Double amount);
}
