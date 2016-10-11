package zx.codec;

import com.datalook.gain.model.RedisProto;
import com.datalook.gain.util.ProtobufUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import redis.clients.util.SafeEncoder;

/**
 * 功能描述：可以作为例子
 * 实现codec转换为redisdatadecode
 * 时间：2016/3/29 14:04
 *
 * @author ：zhaokuiqiang
 */
public class ProtobufDecode implements Codec{

    @Override
    public String decodeValue(byte[] source) {
        try {
            return ProtobufUtil.decodeUser(source).toString();
        } catch(InvalidProtocolBufferException e) {
            return DefaultDecode.DEFAULT.decodeValue(source).toString();
        }
    }

    @Override
    public byte[] encodeValue(Object source) {
        return ProtobufUtil.encodeUser((RedisProto.User) source);
    }

    @Override
    public String decodeField(byte[] source) {
        return SafeEncoder.encode(source);
    }

    @Override
    public byte[] encodeField(Object source) {
        return SafeEncoder.encode(String.valueOf(source));
    }
}
