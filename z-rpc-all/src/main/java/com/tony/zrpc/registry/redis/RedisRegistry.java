package com.tony.zrpc.registry.redis;

import com.tony.zrpc.registry.NotifyListener;
import com.tony.zrpc.registry.RegistryService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 注册中心的redis实现类
 * @author herrhu
 * @date 2021/12/17 15:24
 **/
public class RedisRegistry implements RegistryService {

    // redis host port
    URI address;

    // 心跳定义为15秒
    private static final int TIME_OUT = 15;

    // 维护心跳定时线程池
    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5);

    // 服务消费者 -- 本地副本 service -> redis中匹配的keys
    Map<String, Set<URI>> localCache = new ConcurrentHashMap<>();

    // redis发布订阅
    JedisPubSub jedisPubSub;

    // service -> notifyListener
    Map<String, NotifyListener> listenerMap = new ConcurrentHashMap<>();

    // 服务提供者的信息
    ArrayList<URI> serviceHeartBeat = new ArrayList<>();

    @Override
    public void register(URI uri) {
        String key = "zrpc:" + uri.toString();
        System.out.println("redis 注册的服务：" + key);
        Jedis jedis = new Jedis(address.getHost(), address.getPort());
        jedis.setex(key, TIME_OUT, String.valueOf(System.currentTimeMillis()));
        jedis.close();
        // 开始心跳机制
        serviceHeartBeat.add(uri);
    }

    @Override
    public void subscriber(String service, NotifyListener notifyListener) throws URISyntaxException {
        // 代表第一次初始化订阅
        if (localCache.get(service) == null) {
            localCache.putIfAbsent(service, new HashSet<>());
            // 第一次应该主动的去redis
            // 默认连接 127.0.0.1 6379
            Jedis jedis = new Jedis(address.getHost(), address.getPort());
            Set<String> serviceInstance = jedis.keys("zrpc:*" + service + "/*");
            for (String serviceUri : serviceInstance) {
                localCache.get(service).add(new URI(serviceUri));
            }

            // 调用回调通知
            notifyListener.notify(localCache.get(service));
            listenerMap.putIfAbsent(service, notifyListener);
            jedis.close();
        }
    }

    @Override
    public void init(URI address) {
        this.address = address;

        // 定时任务
        executorService.scheduleAtFixedRate(new Runnable() {

            // 通过不让当前的key expire方式，其实可以更新时间戳的方式
            @Override
            public void run() {
                Jedis jedis = new Jedis(address.getHost(), address.getPort());
                for (URI uri : serviceHeartBeat) {
                    String key = "zrpc:" + uri.toString();
                    jedis.expire(key, TIME_OUT);
                }
            }
        }, 3000, 5000, TimeUnit.MICROSECONDS);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                jedisPubSub = new JedisPubSub() {
                    /**
                     * 订阅的频道收到消息时
                     * @param pattern 频道匹配格式
                     * @param channel 频道
                     * @param message 消息
                     */
                    @Override
                    public void onPMessage(String pattern, String channel, String message) {
                        try {
                            // 去掉头部
                            URI serviceURI = new URI(channel.replace("__keyspace@0__:", ""));
                            String serviceName = serviceURI.getPath().replace("/", "");
                            if("set".equals(message)) {
                                // 本地增加 一个服务实例信息
                                Set<URI> uris = localCache.get(serviceName);
                                // 收到的服务实例变动，可能与此消费者无关，此处null判断
                                if(uris != null) {
                                    uris.add(serviceURI);
                                }
                            }
                            // http://wwww.xxx.com:80/path
                            if("expired".equals(message)) {
                                // 过期
                                Set<URI> uris = localCache.get(serviceName);
                                // 收到的服务实例变动，可能与此消费者无关，此处null判断
                                if(uris != null) {
                                    uris.remove(serviceURI);
                                }
                            }
                            // 既然本地数据 localCache 发生了变化， 此时就要通知
                            if("expired".equals(message) || "set".equals(message)) {
                                NotifyListener notifyListener = listenerMap.get(serviceName);
                                if(notifyListener != null) {
                                    notifyListener.notify(localCache.get(serviceName));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                Jedis jedis = new Jedis(address.getHost(), address.getPort());
                // 监听该频道 psubscribe process with patterns
                jedis.psubscribe(jedisPubSub, "__keyspace@0__:zrpc:*");
                jedis.close();
            }
        });
    }
}
