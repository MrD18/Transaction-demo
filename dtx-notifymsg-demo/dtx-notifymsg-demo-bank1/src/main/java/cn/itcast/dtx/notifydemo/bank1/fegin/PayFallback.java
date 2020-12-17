package cn.itcast.dtx.notifydemo.bank1.fegin;

import cn.itcast.dtx.notifydemo.bank1.entity.AccountPay;
import org.springframework.stereotype.Component;

/**调用失败后方法降级
 * @author: duhao
 * @date: 2020/12/16 15:10
 */
@Component
public class PayFallback  implements PayClient{
    @Override
    public AccountPay payresult(String txNo) {
        AccountPay accountPay = new AccountPay();
        accountPay.setResult("fail");
        return accountPay;
    }
}
