package zx.util;

import com.google.protobuf.InvalidProtocolBufferException;
import zx.codec.Codec;
import zx.codec.CodecEnum;
import zx.codec.DefaultDecode;

/**
 * 解码工具类
 * 时间：2016-10-13
 *
 * @author: zhaokuiqiang
 */
public class CodecUtil {

    public static String decode(Object source){
        if(source == null){
            return "null";
        }
        if(source instanceof String){
            return source.toString();
        }else{
            return decode((byte)source);
        }
    }

    /**
     * 尝试解码
     * @param source
     * @return
     */
    public static String decode(byte [] source){
        CodecEnum [] codecs = CodecEnum.values();
        String result = null;
        for(CodecEnum codec : codecs) {
            try {
                result = codec.decode(source);
                break;
            } catch(InvalidProtocolBufferException e) {
            } catch(RuntimeException e){
                e.printStackTrace();
                result = "解码失败,解码类型："+codec.name();
                break;
            }
        }
        //未解码，使用默认解码方式解码
        if(result == null){
            result = DefaultDecode.DEFAULT.decode(source);
        }
        return result;
    }

    /**
     * 使用指定方式编码
     * @param source
     * @param codec
     * @return
     */
    public static byte[] encode(Object source,Codec codec) {
        return codec.encode(source);
    }
}
