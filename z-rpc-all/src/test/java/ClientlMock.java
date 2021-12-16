import com.tony.zrpc.common.serialize.json.JsonSerialization;
import com.tony.zrpc.common.tools.ByteUtil;
import com.tony.zrpc.provider.server.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.Socket;

/**
 * 测试自定义协议
 */
public class ClientlMock {
    final static byte[] MAGIC = new byte[]{(byte) 0xda, (byte) 0xbb};
    public static void main(String[] args) throws Exception {
        // 1. body
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setClassName("com.tony.edu.rpc.sms.api.SmsService");
        rpcRequest.setMethodName("send");
        rpcRequest.setParameterTypes(new Class[]{String.class,String.class});
        rpcRequest.setArguments(new Object[]{"13800138000", "iamtony"});
        byte[] body = new JsonSerialization().serialize(rpcRequest);
        System.out.println(body.length + " - request-body:" + new String(body));
        // build request
        // 2. header
        ByteBuf requestBuffer = Unpooled.buffer();
        requestBuffer.writeByte(MAGIC[0]);
        requestBuffer.writeByte(MAGIC[1]);
        // 3. length
        int len = body.length;
        byte[] lenBytes = ByteUtil.int2bytes(len);
        requestBuffer.writeBytes(lenBytes);
        // 4. body
        requestBuffer.writeBytes(body);
        System.out.println("request length:" + requestBuffer.readableBytes());

        // client
        Socket client = new Socket("127.0.0.1", 8080);
        byte[] req = new byte[requestBuffer.readableBytes()];
        requestBuffer.readBytes(req);

        for (int i = 0; i < 10; i++) {
            client.getOutputStream().write(req);
        }

        System.in.read();
    }
}
