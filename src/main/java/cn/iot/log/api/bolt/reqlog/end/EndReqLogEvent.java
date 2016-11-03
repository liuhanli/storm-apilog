package cn.iot.log.api.bolt.reqlog.end;

import java.util.Map;

import cn.iot.log.api.bolt.AbstractBoltLogEvent;

/**
 * The Class EndReqLogEvent.
 */
public class EndReqLogEvent extends AbstractBoltLogEvent {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The host. */
	private String host;

	/** The module. */
	private String module;

	/** The day. */
	private String day;

	/** The bolt time. */
	private long boltTime;

	/** The map. */
	private Map<String, Object> map;

	/** The transid. */
	private String transid;

	/**
	 * Instantiates a new end req log event.
	 *
	 * @param host the host
	 * @param module the module
	 * @param day the day
	 * @param map the map
	 * @param transid the transid
	 */
	public EndReqLogEvent(String host, String module, String day, Map<String, Object> map, String transid) {
		this.host = host;
		this.module = module;
		this.day = day;
		this.boltTime = System.currentTimeMillis();
		this.map = map;
		this.transid = transid;
	}

	/**
	 * Gets the transid.
	 *
	 * @return the transid
	 */
	public String getTransid() {
		return transid;
	}

	/* (non-Javadoc)
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

}
