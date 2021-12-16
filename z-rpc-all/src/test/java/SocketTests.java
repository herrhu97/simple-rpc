import com.tony.zrpc.common.serialize.json.JsonSerialization;
import com.tony.zrpc.provider.server.RpcRequest;
import com.tony.zrpc.provider.server.RpcResponse;

import java.net.Socket;

public class SocketTests {
    public static void main(String[] args) throws Exception {
        // client
        Socket client = new Socket("127.0.0.1", 8080);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName("com.tony.edu.rpc.sms.api.SmsService");
        rpcRequest.setMethodName("send");
        rpcRequest.setParameterTypes(new Class[]{String.class,String.class});
        rpcRequest.setArguments(new Object[]{"13800138000", "iamtony"});


        client.getOutputStream().write(new JsonSerialization().serialize(rpcRequest));

        // send sned 1 a b
        byte[] response = new byte[1024];
        client.getInputStream().read(response);

        RpcResponse rpcResponse = (RpcResponse) new JsonSerialization().deserialize(response, RpcResponse.class);
        System.out.println(rpcResponse);
        client.close();
    }
}
