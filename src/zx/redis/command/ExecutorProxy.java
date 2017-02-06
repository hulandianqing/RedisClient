package zx.redis.command;

import com.datalook.gain.jedis.command.Command;
import com.datalook.gain.jedis.command.executor.CommandExecutor;
import com.datalook.gain.jedis.command.executor.CommandMultiExecutor;
import com.datalook.gain.jedis.command.executor.Executor;
import com.datalook.gain.jedis.result.JedisResult;
import redis.clients.jedis.Jedis;
import zx.jedis.JedisFactory;
import zx.util.CodecUtil;
import zx.util.ConsoleUtil;

import java.util.List;
import java.util.Queue;

/**
 * 功能描述：执行的代理类
 * 时间：2016/10/16 21:19
 *
 * @author ：zhaokuiqiang
 */
public class ExecutorProxy extends Executor{

    Executor executor;
    String id;
    boolean isMulti;

    /**
     * 是否muliti操作
     * @param id
     * @param isMulti
     */
    public ExecutorProxy(String id,boolean isMulti) {
        this.id = id;
        this.isMulti = isMulti;
        if(isMulti){
            executor = new CommandMultiExecutor(JedisFactory.getJedis(id));
        }else{
            executor = new CommandExecutor(JedisFactory.getJedis(id));
        }
    }

    @Override
    public Executor execute() {
        begin();
        executor.execute();
        end();
        return this;
    }

    @Override
    public Jedis getJedis() {
        return executor.getJedis();
    }

    @Override
    public Executor setJedis(Jedis jedis) {
        executor.setJedis(jedis);
        return this;
    }

    @Override
    public void executeAndClose() {
        executor.executeAndClose();
    }

    @Override
    public JedisResult getResult() {
        return executor.getResult();
    }

    @Override
    public Queue<JedisResult> getResults() {
        return executor.getResults();
    }

    @Override
    public Executor addCommand(Command command) {
        executor.addCommand(command);
        return this;
    }

    @Override
    public Executor addCommands(List<Command> list) {
        executor.addCommands(list);
        return this;
    }

    @Override
    public Executor addCommands(Command... commands) {
        executor.addCommands(commands);
        return this;
    }

    @Override
    public Queue<Command> getCommands() {
        return executor.getCommands();
    }

    @Override
    public void close() {
    	String redisDetail = executor.getJedis().getClient().getHost() + ":" + executor.getJedis().getClient().getPort();
		executor.close();
		ConsoleUtil.write("关闭连接"+redisDetail);
	}

    public void begin(){

    }

    public void end(){
		ConsoleUtil.write("命令执行完成");
    }
}
