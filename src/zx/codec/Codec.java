package zx.codec;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 功能描述：编码解码需要实现此接口
 * 时间：2016/3/29 14:01
 *
 * @author ：zhaokuiqiang
 */
public interface Codec {
    public String decode(byte [] source) throws InvalidProtocolBufferException;
    public byte[] encode(Object source);

}
