package zx.util;

import com.datalook.gain.jedis.command.common.CommandKeys;
import com.datalook.gain.jedis.command.common.CommandSelect;
import com.datalook.gain.jedis.command.common.CommandType;
import com.datalook.gain.jedis.command.executor.CommandExecutor;
import com.datalook.gain.jedis.command.executor.CommandMultiExecutor;
import com.datalook.gain.jedis.command.executor.Executor;
import com.datalook.gain.jedis.command.hash.*;
import com.datalook.gain.jedis.command.set.CommandGet;
import com.datalook.gain.jedis.command.set.CommandMGet;
import com.datalook.gain.jedis.command.set.CommandSet;
import com.datalook.gain.jedis.result.JedisResult;
import com.datalook.gain.util.ValidateUtils;
import com.google.protobuf.InvalidProtocolBufferException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import redis.clients.jedis.Response;
import zx.codec.DefaultDecode;
import zx.constant.Constant;
import zx.design.Main;
import zx.jedis.JedisFactory;
import zx.model.TableData;

import java.util.*;

/**
 * 功能描述：
 * 时间：2016/3/29 11:23
 *
 * @author ：zhaokuiqiang
 */
public class JedisUtil {

    /**
     * 存储当前db的key和field
     */
    public final static Map<String,ObservableList<String>> CURRENTKEYFIELDS = new HashMap<>();

    public static HashSet<String> getAllKey(String id,int index){
        Executor executor = getExecutor(id);
        JedisResult jedisResult = executor.addCommand(new CommandSelect(index)).execute().getResult();
        if(!Constant.REDIS_OK.equals(jedisResult.getResult().toString())){
            return new HashSet<>();
        }
        // hgetall所有值 keys * mget
        jedisResult = executor.addCommands(new CommandKeys("*")).execute().getResult();
        return (HashSet<String>) jedisResult.getResult();
    }

    /**
     * 获取所有类型是string的value
     * @return
     */
    public static List<TableData> getAllKeyValue(String id, int index){
        List<TableData> result = new ArrayList<>();
        HashSet<String> set = getAllKey(id,index);
        if(set.size() > 0){
            Iterator<String> iterator = set.iterator();
            List<String> hashKeys = new ArrayList<>();
            List<String> stringKeys = new ArrayList<>();
            while(iterator.hasNext()){
                String key = iterator.next();
                String type = getKeyType(getExecutor(id),key);
                if(Constant.REDIS_HASH.equals(type)){
                    hashKeys.add(key);
                }else if(Constant.REDIS_STRING.equals(type)){
                    stringKeys.add(key);
                }
            }
            //遍历string
            List<String> stringValues = getAllString(id,stringKeys.toArray(new String[]{}));
            for(int i = 0; i < stringKeys.size(); i++) {
                if(stringValues.get(i) != null){
                    TableData tableData = new TableData();
                    tableData.setKey(stringKeys.get(i));
                    StringBuilder sb = new StringBuilder();
                    tableData.setValue(stringValues.get(i));
                    tableData.setType(Constant.REDIS_STRING);
                    tableData.setSource(stringKeys.get(i));
                    result.add(tableData);
                }
            }
            //获取hash
            List<Set<byte[]>> hashFields = getAllHash(id,hashKeys);
            if(hashFields != null && hashFields.size() > 0){
                //清空缓存中存储的当前hash数据
                CURRENTKEYFIELDS.clear();
            }
            //遍历hash
            for(int i = 0; i < hashFields.size(); i++) {
                TableData tableData = new TableData();
                tableData.setKey(hashKeys.get(i));
                tableData.setType(Constant.REDIS_HASH);
                Set<byte[]> hashKeySet = hashFields.get(i);
                Iterator<byte[]> hashKeyIt = hashKeySet.iterator();
                StringBuilder sb = new StringBuilder();
//                List<RedisProto.User> sourceData = new ArrayList<>();
                ObservableList<String> observableList = FXCollections.observableArrayList();
                while(hashKeyIt.hasNext()){
                    byte[] hashFiels = hashKeyIt.next();
                    String decodeFields = Main.CODEC.decodeField(hashFiels);
                    sb.append(decodeFields);
                    tableData.addField(sb.toString());
                    if(hashKeyIt.hasNext()){
                        sb.append(" & ");
                    }
                    observableList.add(decodeFields);
//                    sourceData.add(user);
//                    tableData.setSource(sourceData);
                    //不显示value
                    /*sb.append("value:");
                    try {
                        RedisProto.User user = CODEC.decode(maps.get(hashFiels));
                        sb.append("{").append(TextFormat.printToUnicodeString(user).replace("\n","")).append("}");
                        tableData.setSource(user);
                    } catch(InvalidProtocolBufferException e) {
                        sb.append(DefaultDecode.DEFAULT.decode(maps.get(hashFiels)));
                        tableData.setSource(DefaultDecode.DEFAULT.decode(maps.get(hashFiels)));
                    }*/
                }
                CURRENTKEYFIELDS.put(tableData.getKey(),observableList);
                tableData.setValue(sb.toString());
                result.add(tableData);
            }
        }
        return result;
    }

    /**
     * 获取所有类型是string的value
     * @return
     */
    public static List<String> getAllString(String id,String [] keys){
        if(keys == null || keys.length == 0){
            return new ArrayList<>();
        }
        Executor executor = getExecutor(id);
        JedisResult jedisResult = executor.addCommands(new CommandMGet(keys)).execute().getResult();
        return (List<String>) jedisResult.getResult();
    }

    /**
     * 获取所有类型是string的value
     * @return
     */
    public static List<Set<byte[]>> getAllHash(String id, List<String> keys){
        if(keys == null || keys.size() == 0){
            return new ArrayList<>();
        }
        Executor executor = getExecutorMulti(id);
        for(int i = 0; i < keys.size(); i++) {
            // 改为获取hash的field
//            executor.addCommands(new CommandHashGetAll(keys.get(i)));
            executor.addCommands(new CommandHashKeys(keys.get(i)));
        }
        JedisResult jedisResult = executor.execute().getResult();
        Response<List<Set<byte[]>>> result = (Response<List<Set<byte[]>>>) jedisResult.getResult();
        return result.get();
    }

    /**
     * 获取当前库key的类型
     * @return
     */
    public static String getKeyType(String id,String key){
        return getKeyType(getExecutor(id),key);
    }

    /**
     * 获取当前库key的类型
     * @return
     */
    public static String getKeyType(Executor executor,String key){
        JedisResult jedisResult = executor.addCommand(new CommandType(key)).execute().getResult();
        return String.valueOf(jedisResult.getResult());
    }

    public static String getHashValue(String id,String key,String field){
        Executor executor = getExecutor(id);
        JedisResult jedisResult = executor.addCommand(new CommandHashGet(key,field)).execute().getResult();
        if(jedisResult.getResult() != null){
            return Main.CODEC.decodeValue((byte[]) jedisResult.getResult());
        }else{
            return null;
        }
    }

    /**
     * 如果field为空则执行get否则执行hget
     * @param id
     * @param key
     * @param field
     * @return
     */
    public static String getValue(String id,String key,String field){
        if(ValidateUtils.isEmpty(id) || ValidateUtils.isEmpty(key)){
            return null;
        }
        Executor executor = getExecutor(id);
        JedisResult jedisResult;
        if(ValidateUtils.isEmpty(field)){
            jedisResult = executor.addCommand(new CommandGet(key)).execute().getResult();
        }else{
            jedisResult = executor.addCommand(new CommandHashGet(key,field)).execute().getResult();
        }
        if(jedisResult.getResult() != null){
            if(jedisResult.getResult() instanceof byte[]){
                return Main.CODEC.decodeValue((byte[]) jedisResult.getResult());
            }else{
                return (String) jedisResult.getResult();
            }
        }else{
            return null;
        }
    }

    /**
     * 保存string
     * @param key
     * @param value
     */
    public static boolean saveData(String key,String value){
        if(ValidateUtils.isEmpty(Main.redisId)){
            return false;
        }
        if(ValidateUtils.isEmpty(key) || ValidateUtils.isEmpty(value)){
            return false;
        }
        Executor executor = getExecutor(Main.redisId);
        try {
            executor.addCommand(new CommandSet(key,value)).execute();
        }catch(RuntimeException e){
            return false;
        }
        return true;
    }

    /**
     * 保存hash
     * @param key
     * @param field
     * @param value
     */
    public static boolean saveData(String key,String field,String value){
        if(ValidateUtils.isEmpty(Main.redisId)){
            return false;
        }
        if(ValidateUtils.isEmpty(key) || ValidateUtils.isEmpty(field)
                 || ValidateUtils.isEmpty(value)){
            return false;
        }
        Executor executor = getExecutor(Main.redisId);
        try {
            executor.addCommand(new CommandHashSet(key,field,value)).execute();
        }catch(RuntimeException e){
            return false;
        }
        return true;
    }

    public static Executor getExecutorMulti(String id){
        return new CommandMultiExecutor(JedisFactory.getJedis(id));
    }
    public static Executor getExecutor(String id){
        return new CommandExecutor(JedisFactory.getJedis(id));
    }

}
