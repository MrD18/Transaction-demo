package cn.itcast.dtx.tccdemo.bank1.feign;

import jdk.nashorn.internal.objects.annotations.Getter;
import org.dromara.hmily.annotation.Hmily;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: duhao
 * @date: 2020/12/14 18:10
 */
@FeignClient(value = "tcc-demo-bank2",fallback =Bank2Fallback.class )
public interface Bank2Client {
    //远程调用李四的微服务
    @GetMapping("/bank2/transfer")
    @Hmily
    public  String transfer(@RequestParam("amount") Double amount);
}
