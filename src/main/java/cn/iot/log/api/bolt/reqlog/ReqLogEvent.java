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

	/**
	 * Instantiates a new req log event.
	 *
	 * @param host the host
	 * @param module the module
	 * @param day the day
	 * @param map the map
	 */
	public ReqLogEvent(String host, String module, String day, Map<String, Object> map) {
		this.host = host;
		this.module = module;
		this.day = day;
		this.boltTime = System.currentTimeMillis();
		this.map = map;
	}

	/** The map. */
	private Map<String, Object> map;

	/* (non-Javadoc)
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getHost()
	 */
	@Override
	public String getHost() {
		return host;
	}

	/* (non-Javadoc)
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getModule()
	 */
	@Override
	public String getModule() {
		return module;
	}

	/* (non-Javadoc)
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getDay()
	 */
	@Override
	public String getDay() {
		return day;
	}

	/* (non-Javadoc)
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getBoltTime()
	 */
	@Override
	public long getBoltTime() {
		return boltTime;
	}

	/* (non-Javadoc)
	 * @see cn.iot.log.api.bolt.AbstractBoltLogEvent#getMap()
	 */
	@Override
	public Map<String, Object> getMap() {
		return null;
	}

}
