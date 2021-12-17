package com.tony.zrpc.registry;

import java.net.URI;
import java.util.Set;

/**
 * 事件监听器
 * @author herrhu
 * @date 2021/12/17 15:18
 **/
public interface NotifyListener {

    /**
     * 订阅的数据发生变化，则触发这个方法的执行
     * @param uris
     */
    void notify(Set<URI> uris);
}
