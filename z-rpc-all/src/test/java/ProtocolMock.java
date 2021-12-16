import com.tony.zrpc.common.serialize.json.JsonSerialization;
import com.tony.zrpc.common.tools.ByteUtil;
import com.tony.zrpc.provider.server.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 模拟自定义的协议
 * 【头部 描述】 + 【body】
 */
public class ProtocolMock {
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

        // parse
        if(requestBuffer.readableBytes() <2){
            return;
        }
        // magic number
        byte[] magic = new byte[2];
        requestBuffer.readBytes(magic);
        if(magic[0] != MAGIC[0] || magic[1] != MAGIC[1]){
            return;
        }
        // length
        byte[] lengthBytes = new byte[4];
        requestBuffer.readBytes(lengthBytes);
        int length = ByteUtil.Bytes2Int_BE(lenBytes);

        if(requestBuffer.readableBytes() < length) {
            return;
            //
        } else {
            byte[] bodyBytes = new byte[length];
            requestBuffer.readBytes(bodyBytes);
            RpcRequest request = (RpcRequest) new JsonSerialization().deserialize(bodyBytes, RpcRequest.class);
            System.out.println(request);
        }
    }
}
