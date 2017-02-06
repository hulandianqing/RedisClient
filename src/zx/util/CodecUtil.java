package zx.util;

import com.datalook.gain.jedis.result.JedisResult;
import com.datalook.gain.util.ValidateUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import redis.clients.jedis.Response;
import zx.codec.Codec;
import zx.codec.CodecEnum;
import zx.codec.DefaultDecode;
import zx.model.TableData;

import java.util.*;

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
        String result = "";
        if(source instanceof String){
            result = source.toString();
        }else if(source instanceof Response){
            result = decode(decode((Response<List<Set<byte[]>>>) source));
        }else if(source instanceof Object[]){
            Object[] s = (Object[]) source;
            result = decode(decode((TableData) s[0],(List<Set<byte[]>>) s[1]));
        }else if(source instanceof List){
        	result = decode((List) source);
		}else if(source instanceof byte[]){
            result = decode((byte[])source);
        }else{
		}
		String finalResult = result;
		new Thread(new Runnable() {
			@Override
			public void run() {
				ConsoleUtil.write("解码完成 ： "+finalResult);
			}
		}).start();
		return result;
    }

    public static List<String> decode(Response<List<Set<byte[]>>> response){
        List<String> resultList = new ArrayList<>();
        List<Set<byte[]>> list = response.get();
        for(int i = 0; i < list.size(); i++) {
            Iterator<byte[]> iterator = list.get(i).iterator();
            while(iterator.hasNext()){
                String temp = CodecUtil.decode(iterator.next());
                resultList.add(temp);
            }
        }
        return resultList;
    }

    public static List<TableData> decode(TableData tableData,List<Set<byte[]>> setList){
        List<TableData> resultList = new ArrayList<>();
        for(int i = 0; i < setList.size(); i++) {
            Iterator<byte[]> iterator = setList.get(i).iterator();
            while(iterator.hasNext()){
                String field = DefaultDecode.DEFAULT.decode(iterator.next());
                TableData data = new TableData();
                data.setField(field);
                data.setKey(tableData.getKey());
                data.setType(tableData.getType());
                resultList.add(data);
            }
        }
        return resultList;
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
				if(ValidateUtils.isEmpty(result)){
					continue;
				}
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

    public static String decode(List lists){
        StringBuilder stringBuilder = new StringBuilder();
        for(int j = 0; j < lists.size(); j++) {
            Object obj = lists.get(j);
			if(j != 0){
				stringBuilder.append(",");
			}
            stringBuilder.append(obj.toString());
			if(stringBuilder.length() > 512){
				stringBuilder.append("...");
				return stringBuilder.toString();
			}
        }
        return stringBuilder.toString();
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

	/**
	 * 功能描述：判断是否包含乱码
	 * @return
	 */
	private static boolean hasErrorChar(String text){
		if(!ValidateUtils.isEmpty(text)){
			if(text.indexOf("�") >= 0){
				return true;
			}
		}
		return false;
	}
}
