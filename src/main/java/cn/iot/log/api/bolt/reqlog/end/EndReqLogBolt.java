package cn.iot.log.api.bolt.reqlog.end;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.iot.log.api.bolt.AbstractLogBolt;
import cn.iot.log.api.common.Constants;
import cn.iot.log.api.kafka.LogEvent;

public class EndReqLogBolt extends AbstractLogBolt {
	private static final Logger logger = LoggerFactory.getLogger(EndReqLogBolt.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		Object obj = input.getValue(0);
		if (!(obj instanceof LogEvent)) {
			logger.debug("input Tuple is not a LogEvent instance");
		} else {
			LogEvent logEvent = (LogEvent) obj;
			EndReqLogEvent endReqLogEvent = null;
			try {
				endReqLogEvent = parseLog(logEvent);
			} catch (Exception e) {
				logger.error("parse LogEvent to endReqLogEvent exception", e);
			}
			if (endReqLogEvent != null) {
				collector.emit(new Values(endReqLogEvent.getHost(), endReqLogEvent.getTransid(),
						endReqLogEvent));
			}
		}
	}

	private EndReqLogEvent parseLog(LogEvent logEvent) throws Exception {
		if (logEvent == null || isEmpty(logEvent.getBody())) {
			throw new IllegalArgumentException("input LogEvent is null");
		}
		Matcher matcher = getMatcher();
		boolean isMatch = matcher.reset(logEvent.getBody()).matches();
		if (isMatch) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String day = matcher.group(Constants.DAY);
			String transid = matcher.group(Constants.TRANSID);
			for (String key : getKeys()) {
				String value = matcher.group(key);
				dataMap.put(key, value);
			}
			EndReqLogEvent endReqLogEvent = new EndReqLogEvent(logEvent.getHost(),
					logEvent.getModule(), day, dataMap, transid);
			return endReqLogEvent;
		} else {
			return null;
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(getOutPutFieldNames()));
	}

}
