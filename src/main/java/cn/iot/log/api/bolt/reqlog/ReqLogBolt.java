package cn.iot.log.api.bolt.reqlog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.RotatingMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cn.iot.log.api.bolt.AbstractBoltLogEvent;
import cn.iot.log.api.bolt.AbstractLogBolt;
import cn.iot.log.api.bolt.reqlog.end.EndReqLogEvent;
import cn.iot.log.api.bolt.reqlog.start.StartReqLogEvent;
import cn.iot.log.api.common.Constants;

/**
 * The Class ReqLogBolt.
 */
public class ReqLogBolt extends AbstractLogBolt {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ReqLogBolt.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The start cache. */
	private static Cache<String, StartReqLogEvent> startCache;

	/** The end cache. */
	private static Cache<String, EndReqLogEvent> endCache;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		startCache = CacheBuilder.newBuilder().maximumSize(100000)
				.expireAfterWrite(600, TimeUnit.SECONDS).build();
		endCache = CacheBuilder.newBuilder().maximumSize(100000)
				.expireAfterWrite(600, TimeUnit.SECONDS).build();
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
//		logger.info("SourceComponent:{},SourceStreamId():{}", input.getSourceComponent(),
//				input.getSourceStreamId());
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
		String cacheId = event.getTransid();
		startCache.put(cacheId, event);
		EndReqLogEvent endEvent = endCache.getIfPresent(cacheId);
		if (endEvent == null) {
			logger.debug("handleStartReqLog endEvent=null cacheId:{}", cacheId);
		} else {
			if (endEvent instanceof EndReqLogEvent) {
				startCache.invalidate(cacheId);
				endCache.invalidate(cacheId);
				logger.debug("handleStartReqLog createAndSendToSinkHandler");
				createReqLog(event, endEvent, collector);
			} else {
				logger.warn("already exists StartReqLogEvent, transid:{}", cacheId);
			}
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
		String cacheId = event.getTransid();
		StartReqLogEvent startEvent = startCache.getIfPresent(cacheId);
		if (startEvent == null) {
			logger.debug("handleEndReqLog startEvent=null cacheId:{}", cacheId);
			endCache.put(cacheId, event);
		} else {
			if (startEvent instanceof StartReqLogEvent) {
				startCache.invalidate(cacheId);
				endCache.invalidate(cacheId);
				logger.debug("handleEndReqLog createAndSendToSinkHandler");
				createReqLog(startEvent, event, collector);
			} else {
				logger.warn("already exists EndReqLogEvent, transid:{}", cacheId);
			}
		}
	}

	private void createReqLog(StartReqLogEvent startLog, EndReqLogEvent endLog,
			BasicOutputCollector collector) {
		Map<String, Object> map = new HashMap<>();
		map.putAll(startLog.getMap());
		map.putAll(endLog.getMap());
		map.put(Constants.HOST, startLog.getHost());
		map.put(Constants.BOLTTIME, System.currentTimeMillis());
		String host = startLog.getHost();
		String module = Constants.REQLOG;
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
