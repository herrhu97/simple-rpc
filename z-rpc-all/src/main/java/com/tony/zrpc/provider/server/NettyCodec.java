package com.tony.zrpc.provider.server;

import com.tony.zrpc.common.serialize.json.JsonSerialization;
import com.tony.zrpc.common.tools.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.ArrayList;

/**
 * Netty解码和编码都使用此类
 */
// HEADER ( MAGIC_NUMBER(2) + BODY_LENGTH(4) + BODY)
// in ---bytebuf byte[] --> RpcRequest -- decode
// out -- RpcResponse ---> bytebuf byte[] -- encode
public class NettyCodec extends ChannelDuplexHandler {
    Class decodeType;

    public NettyCodec(Class decodeType) {
        this.decodeType = decodeType;
    }

    final static byte[] MAGIC = new byte[]{(byte) 0xda, (byte) 0xbb};

    // in --- client --> server
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        // 1.decode 解码 字节数据变成 java对象
        ArrayList<Object> out = decode(in);
        // 2. fire //每次都会触发后面的handler执行一次
        for (Object o : out) {
            ctx.fireChannelRead(o);
        }

    }

    // 缓存 - 每个链接对应一个 handler解码器对象。一定是一个请求的dabb开头
    ByteBuf tempMsg = Unpooled.buffer();

    /**
     * 实现自定义协议的解析
     * @param in 此次拿到的ByteBuf
     * @return 交给下一个handler的数据
     * @throws Exception 异常
     */
    private ArrayList<Object> decode(ByteBuf in) throws Exception {
        ArrayList<Object> out = new ArrayList<>();
        // merge 合并
        // 此时tempMsg (0, 164, 1024) in (0, 694, 16384)；将被拆包的报文合并 -> tempMsg (0, 860, 1024)
        tempMsg.writeBytes(in);

        for(;;) {
            // 1. parse header 解析头部 - 头部大小 最少6字节
            if (tempMsg.readableBytes() <= 6) {
                byte[] data = new byte[tempMsg.readableBytes()];
                tempMsg.readBytes(data);
                tempMsg.clear();
                tempMsg.writeBytes(data);
                return out;
            }
            // 1.1 find MAGIC 找到数据包的正确开始位置
            byte[] magic = new byte[2];
            tempMsg.readBytes(magic);
            while (true) {
                if (magic[0] != MAGIC[0] || magic[1] != MAGIC[1]) {
                    if (tempMsg.readableBytes() == 0) {
                        tempMsg.clear();
                        tempMsg.writeByte(magic[1]);
                        return out;
                    }
                    magic[0] = magic[1];
                    magic[1] = tempMsg.readByte();
                } else {
                    break;
                }
            }
            // 1.2 body length 读取头部中记录的数据长度
            byte[] lengthBytes = new byte[4];
            tempMsg.readBytes(lengthBytes);
            int length = ByteUtil.Bytes2Int_BE(lengthBytes);
            // 1.3 body 根据数据长度去读取对应的数据内容，如果发现可读取数据不够，缓存起来，下次数据来的时候合并
            if (tempMsg.readableBytes() < length) {
                // 在此情况下，将tempMsg (866, 1024, 1024) -> tempMsg (0, 164, 1024)，相当于最后164个数组末尾元素移到数组开始处
                byte[] data = new byte[tempMsg.readableBytes()];
                tempMsg.readBytes(data);
                tempMsg.clear();
                tempMsg.writeBytes(magic);
                tempMsg.writeBytes(lengthBytes);
                tempMsg.writeBytes(data);
                return out;
            }
            byte[] body = new byte[length];
            tempMsg.readBytes(body);
            // deserialize
            Object o = new JsonSerialization().deserialize(body, decodeType);
            out.add(o);
            // ...
        }
    }

    // out -- encode
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // 1. serialize 序列化
        byte[] body =new JsonSerialization().serialize(msg);
        // build request
        // 2. header 添加头部信息
        ByteBuf requestBuffer = Unpooled.buffer();
        requestBuffer.writeByte(MAGIC[0]);
        requestBuffer.writeByte(MAGIC[1]);
        // 3. length
        int len = body.length;
        byte[] lenBytes = ByteUtil.int2bytes(len);
        requestBuffer.writeBytes(lenBytes);
        // 4. body
        requestBuffer.writeBytes(body);
        // 5. write
        ctx.write(requestBuffer, promise);
    }
}
