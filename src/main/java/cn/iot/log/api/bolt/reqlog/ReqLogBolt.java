package cn.iot.log.api.bolt.reqlog;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.RotatingMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.iot.log.api.bolt.AbstractBoltLogEvent;
import cn.iot.log.api.bolt.AbstractLogBolt;
import cn.iot.log.api.bolt.reqlog.end.EndReqLogEvent;
import cn.iot.log.api.bolt.reqlog.start.StartReqLogEvent;

/**
 * The Class ReqLogBolt.
 */
public class ReqLogBolt extends AbstractLogBolt {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ReqLogBolt.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The log map. */
	private RotatingMap<String, AbstractBoltLogEvent> logMap;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		logMap = new RotatingMap<>(100000, new ExpireCallback());
	}

	/**
	 * Execute.
	 *
	 * @param input
	 *            the input
	 * @param collector
	 *            the collector
	 */
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		Object obj = input.getValue(2);
		if (obj instanceof StartReqLogEvent) {
			handlerStartReqLog((StartReqLogEvent) obj, collector);
		} else if (obj instanceof EndReqLogEvent) {
			handlerEndReqLog((EndReqLogEvent) obj, collector);
		} else {
			logger.warn("this obj is not type of start or end reqlog event");
		}
	}

	/**
	 * Handler start req log.
	 *
	 * @param event
	 *            the event
	 * @param collector
	 *            the collector
	 */
	private void handlerStartReqLog(StartReqLogEvent event, BasicOutputCollector collector) {
		Object obj = logMap.get(event.getTransid());
		if (obj == null) {
			logMap.put(event.getTransid(), event);
			return;
		}
		if (obj instanceof EndReqLogEvent) {
			logMap.remove(event.getTransid());
			EndReqLogEvent endLog = (EndReqLogEvent) obj;
			createReqLog(event, endLog, collector);
		} else if (obj instanceof StartReqLogEvent) {
			logger.warn("already exists StartReqLogEvent,transid:{}",((StartReqLogEvent)obj).getTransid());
		} else {
			logger.warn("unkown log type");
		}
	}

	/**
	 * Handler end req log.
	 *
	 * @param event
	 *            the event
	 * @param collector
	 *            the collector
	 */
	private void handlerEndReqLog(EndReqLogEvent event, BasicOutputCollector collector) {
		Object obj = logMap.get(event.getTransid());
		if (obj == null) {
			logMap.put(event.getTransid(), event);
			return;
		}
		if (obj instanceof EndReqLogEvent) {
			logger.warn("already exists EndReqLogEvent,transid:{}",((EndReqLogEvent)obj).getTransid());
		} else if (obj instanceof StartReqLogEvent) {
			logMap.remove(event.getTransid());
			StartReqLogEvent startLog = (StartReqLogEvent) obj;
			createReqLog(startLog, event, collector);
		} else {
			logger.warn("unkown log type");
		}
	}

	private void createReqLog(StartReqLogEvent startLog, EndReqLogEvent endLog,
			BasicOutputCollector collector) {
		Map<String, Object> map = new HashMap<>();
		map.putAll(startLog.getMap());
		map.putAll(endLog.getMap());
		String host = startLog.getHost();
		String module = startLog.getModule();
		String day = startLog.getDay();
		String transid = startLog.getTransid();
		ReqLogEvent event = new ReqLogEvent(host, module, day, map, transid);
		collector.emit(new Values(event.getHost(), event.getModule(), event.getDay(),
				event.getTransid(), event));
	}

	/**
	 * Declare output fields.
	 *
	 * @param declarer
	 *            the declarer
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(getOutPutFieldNames()));
	}

	private class ExpireCallback
			implements RotatingMap.ExpiredCallback<String, AbstractBoltLogEvent> {

		@Override
		public void expire(String key, AbstractBoltLogEvent val) {
			logger.warn("key:{} is expired", key);
		}
	}
}
