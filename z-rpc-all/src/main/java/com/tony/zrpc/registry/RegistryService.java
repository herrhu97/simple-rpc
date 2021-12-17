package com.tony.zrpc.registry;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author herrhu
 * @date 2021/12/17 15:16
 **/
public interface RegistryService {
    /**
     * 注册
     * @param uri
     */
    public void register(URI uri);

    /**
     * 订阅 监听器
     * @param service
     * @param listener
     * @throws URISyntaxException
     */
    public void subscriber(String service, NotifyListener listener) throws URISyntaxException;

    /**
     * 配置注册中心的链接信息
     * @param address
     */
    public void init(URI address);
}
