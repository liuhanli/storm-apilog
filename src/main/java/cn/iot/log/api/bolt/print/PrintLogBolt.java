package cn.iot.log.api.bolt.print;

import java.util.List;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.iot.log.api.bolt.AbstractBoltLogEvent;

public class PrintLogBolt extends BaseBasicBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(PrintLogBolt.class);

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		List<Object> list = input.getValues();
		if (list != null) {
			for (Object obj : list) {
				if (obj instanceof AbstractBoltLogEvent) {
					logger.info("print log:{}", obj.toString());
				}
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("print"));
	}

}
