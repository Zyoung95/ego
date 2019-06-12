package ego.sms.service.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import ego.sms.service.config.SmsProperties;
import ego.sms.service.utils.SmsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {
    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    private SmsProperties prop;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ego.sms.queue", durable = "true"),
            exchange = @Exchange(value = "ego.sms.exchange",
                    ignoreDeclarationExceptions = "true"),
            key = {"user.code"}))
    public void listenSms(Map<String, String> msg) throws Exception {
        if (msg == null || msg.size() <= 0) {
// 放弃处理
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
// 放弃处理
            return;
        }
        System.out.println("发送消息");
// 发送消息
        SendSmsResponse resp = smsUtil.sendSms(phone, code,
                prop.getSignName(),
                prop.getVerifyCodeTemplate());
// 发送失败,会自动重发
//throw new RuntimeException();
    }
}
