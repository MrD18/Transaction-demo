package cn.itcast.dtx.notifydemo.bank1.fegin;

import cn.itcast.dtx.notifydemo.bank1.entity.AccountPay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**远程调用查询接口
 * @author: duhao
 * @date: 2020/12/16 15:08
 */
@FeignClient(value = "dtx-notifymsg-demo-pay",fallback =PayFallback.class )
public interface PayClient {

    @GetMapping("/pay/payresult/{txNo}")
    public AccountPay payresult(@PathVariable("txNo") String txNo);
}
