package cn.iot.log.api.bolt.translog;

import java.io.Serializable;
import java.util.Map;

import cn.iot.log.api.bolt.AbstractBoltLogEvent;

/**
 * 业务流水日志.
 */
public class TransLogEvent extends AbstractBoltLogEvent implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The host. */
	private String host;

	/** The module. */
	private String module;

	/** The day. */
	private String day;

	private String transid;

	/** The bolt time. */
	private long boltTime;

	/** The map. */
	private Map<String, Object> map;

	/**
	 * Instantiates a new trans log event.
	 *
	 * @param host
	 *            the host
	 * @param module
	 *            the module
	 * @param day
	 *            the day
	 * @param map
	 *            the map
	 */
	public TransLogEvent(String host, String module, String day, String transid,
			Map<String, Object> map) {
		this.host = host;
		this.module = module;
		this.day = day;
		this.transid = transid;
		this.boltTime = System.currentTimeMillis();
		this.map = map;
	}

	public String getTransid() {
		return transid;
	}

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

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("host:" + getHost());
		buffer.append(",module:" + getModule());
		buffer.append(",day:" + getDay());
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
