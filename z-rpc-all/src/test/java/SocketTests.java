import com.tony.zrpc.common.serialize.json.JsonSerialization;
import com.tony.zrpc.provider.server.RpcResponse;

import java.io.IOException;
import java.net.Socket;

public class SocketTests {
    public static void main(String[] args) throws Exception {
        // client
        Socket client = new Socket("127.0.0.1", 8080);
        String request = "hellotony";
        client.getOutputStream().write(request.getBytes());

        // send sned 1 a b
        byte[] response = new byte[1024];
        client.getInputStream().read(response);

        RpcResponse rpcResponse = (RpcResponse) new JsonSerialization().deserialize(response, RpcResponse.class);
        System.out.println(rpcResponse);
        client.close();

    }
}
