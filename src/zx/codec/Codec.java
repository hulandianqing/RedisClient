package zx.codec;

/**
 * 功能描述：编码解码需要实现此接口
 * 时间：2016/3/29 14:01
 *
 * @author ：zhaokuiqiang
 */
public interface Codec {
    public String decodeValue(byte [] source);
    public byte[] encodeValue(Object source);

    public String decodeField(byte [] source);
    public byte[] encodeField(Object source);
}
