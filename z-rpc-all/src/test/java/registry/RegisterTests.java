package registry;

import com.tony.zrpc.registry.NotifyListener;
import com.tony.zrpc.registry.redis.RedisRegistry;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * redis做注册中心测试类
 * @author herrhu
 */
public class RegisterTests {
    public static void main(String[] args) throws URISyntaxException {
        RedisRegistry redisRegistry = new RedisRegistry();
        redisRegistry.init(new URI("redis://127.0.0.1:6379"));
        // 服务启动的时候
        redisRegistry.register(new URI("//127.0.0.1:10088/com.tony.edu.sms.api.SmsService/"));

        // 消费者启动的时候
        redisRegistry.subscriber("com.tony.edu.sms.api.SmsService", new NotifyListener() {
            @Override
            public void notify(Set<URI> uris) {
                System.out.println("服务信息有变化："+uris);
            }
        });
    }
}
