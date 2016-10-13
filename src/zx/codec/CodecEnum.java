package zx.codec;

import com.datalook.gain.model.AccountProto;
import com.datalook.gain.model.RedisProto;
import com.datalook.gain.util.AccountProtoUtil;
import com.datalook.gain.util.ProtobufUtil;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * 时间：2016-10-13
 *
 * @author: zhaokuiqiang
 */
public enum CodecEnum implements Codec{

    ACCOUNT{
        public String decode(byte[] source) throws InvalidProtocolBufferException {
            return AccountProtoUtil.decode(source).toString();
        }

        public byte[] encode(Object source) {
            return AccountProtoUtil.encode((AccountProto.Account) source);
        }
    },
    ADDMONEY{
        public String decode(byte[] source) throws InvalidProtocolBufferException {
            return ProtobufUtil.decodeUser(source).toString();
        }

        public byte[] encode(Object source) {
            return ProtobufUtil.encodeUser((RedisProto.User) source);
        }
    };

}
