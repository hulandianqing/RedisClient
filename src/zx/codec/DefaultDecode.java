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
    public String decode(byte[] source) {
        return SafeEncoder.encode(source);
    }

    @Override
    public byte[] encode(Object source) {
        return SafeEncoder.encode(String.valueOf(source));
    }

    private DefaultDecode(){}
}
