package cn.iot.log.api.bolt.reqlog;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import cn.iot.log.api.bolt.AbstractLogBolt;

public class ReqLogBolt extends AbstractLogBolt {

	private static final long serialVersionUID = 1L;

	/** 匹配交易日志解析的正则表达式. */
	private String REGEX;

	/** 输出字段名称. */
	private String outPutFieldName;
	
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public String getRegex() {
		return null;
	}

}
