package cn.iot.log.api.bolt.reqlog;

import java.util.Map;

import cn.iot.log.api.bolt.AbstractBoltLogEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class ReqLogEvent.
 */
public class ReqLogEvent extends AbstractBoltLogEvent {

	/** The host. */
	private String host;

	/** The module. */
	private String module;

	/** The day. */
	private String day;

	/** The bolt time. */
	private long boltTime;

	/** The transid. */
	private String transid;

	/**
	 * Instantiates a new req log event.
	 *
	 * @param host
	 *            the host
	 * @param module
	 *            the module
	 * @param day
	 *            the day
	 * @param map
	 *            the map
	 * @param transid
	 *            the transid
	 */
	public ReqLogEvent(String host, String module, String day, Map<String, Object> map,
			String transid) {
		this.host = host;
		this.module = module;
		this.day = day;
		this.boltTime = System.currentTimeMillis();
		this.map = map;
		this.transid = transid;
	}

	/** The map. */
	private Map<String, Object> map;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getHost()
	 */
	@Override
	public String getHost() {
		return host;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getModule()
	 */
	@Override
	public String getModule() {
		return module;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getDay()
	 */
	@Override
	public String getDay() {
		return day;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getBoltTime()
	 */
	@Override
	public long getBoltTime() {
		return boltTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getMap()
	 */
	@Override
	public Map<String, Object> getMap() {
		return map;
	}

	/**
	 * Gets the transid.
	 *
	 * @return the transid
	 */
	public String getTransid() {
		return transid;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("host:" + getHost());
		buffer.append(",module:" + getModule());
		buffer.append(",day:" + getDay());
		buffer.append(",transid:" + getTransid());
		Map<String, Object> map = getMap();
		if (map != null) {
			for (Map.Entry<String, Object> entry : getMap().entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				buffer.append("," + key + ":" + value);
			}
		}
		return buffer.toString();
	}

}
