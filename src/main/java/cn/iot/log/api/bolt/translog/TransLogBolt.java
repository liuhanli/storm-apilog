package cn.iot.log.api.bolt.translog;

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

/**
 * The Class TransLogBolt.
 */
public class TransLogBolt extends AbstractLogBolt {
	private static final Logger logger = LoggerFactory.getLogger(TransLogBolt.class);
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** 匹配交易日志解析的正则表达式. */
	private String REGEX;

	/** 输出字段名称. */
	private String outPutFieldName;

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		Object obj = input.getValue(0);
		if (!(obj instanceof LogEvent)) {
			logger.debug("input Tuple is not a LogEvent instance");
		} else {
			LogEvent logEvent = (LogEvent) obj;
			TransLogEvent transLogEvent = null;
			try {
				transLogEvent = parseLog(logEvent);
			} catch (Exception e) {
				logger.error("parse LogEvent to TransLogEvent exception", e);
			}
			if (transLogEvent != null) {
				collector.emit(new Values(transLogEvent));
			}
		}
	}

	private TransLogEvent parseLog(LogEvent logEvent) throws Exception {
		if (logEvent == null || isEmpty(logEvent.getBody())) {
			throw new IllegalArgumentException("input LogEvent is null");
		}
		Matcher matcher = getMatcher();
		boolean isMatch = matcher.reset(logEvent.getBody()).matches();
		if (isMatch) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String day = matcher.group(Constants.DAY);
			for (String key : getKeys()) {
				String value = matcher.group(key);
				dataMap.put(key, value);
			}
			dataMap.put(Constants.MESSAGE, logEvent.getBody());
			TransLogEvent transLogEvent = new TransLogEvent(logEvent.getHost(),
					logEvent.getModule(), day, dataMap);
			return transLogEvent;
		} else {
			return null;
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(getOutPutFieldName()));
	}

	@Override
	public String getRegex() {
		return REGEX;
	}

	/**
	 * Sets the regex.
	 *
	 * @param regex
	 *            the new regex
	 */
	public void setRegex(String regex) {
		this.REGEX = regex;
	}

	/**
	 * Gets the out put field name.
	 *
	 * @return the out put field name
	 */
	public String getOutPutFieldName() {
		return outPutFieldName;
	}

	/**
	 * Sets the out put field name.
	 *
	 * @param outPutFieldName
	 *            the new out put field name
	 */
	public void setOutPutFieldName(String outPutFieldName) {
		this.outPutFieldName = outPutFieldName;
	}

}
