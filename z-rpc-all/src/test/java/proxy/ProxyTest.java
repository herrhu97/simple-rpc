package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK动态代理技术测试
 * @author herrhu
 * @date 2021/12/17 11:22
 **/
public class ProxyTest {
    public static void main(String[] args) {
        Hello proxy = (Hello) Proxy.newProxyInstance(ProxyTest.class.getClassLoader(), new Class[]{Hello.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("say")) {
                    System.out.println("say方法被拦截了");
                } else {
                    System.out.println("我被拦截了");
                }

                System.out.println("方法调用参数");
                System.out.println(args);
                return "返回值";
            }
        });

        proxy.say();
        proxy.hello("hhh");
        System.out.println(proxy.get());
    }
}
