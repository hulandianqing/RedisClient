package zx.codec;

import redis.clients.util.SafeEncoder;

/**
 * 功能描述：
 * 时间：2016/3/29 14:04
 *
 * @author ：zhaokuiqiang
 */
public class DefaultDecode implements Codec{

    public static final DefaultDecode DEFAULT = new DefaultDecode();

    @Override
    public String decodeValue(byte[] source) {
        return SafeEncoder.encode(source);
    }

    @Override
    public byte[] encodeValue(Object source) {
        return SafeEncoder.encode(String.valueOf(source));
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
