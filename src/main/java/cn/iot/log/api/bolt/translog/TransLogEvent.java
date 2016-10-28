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
	
	/** The bolt time. */
	private long boltTime;
	
	/** The map. */
	private Map<String, Object> map;

	/**
	 * Instantiates a new trans log event.
	 *
	 * @param host the host
	 * @param module the module
	 * @param day the day
	 * @param map the map
	 */
	public TransLogEvent(String host, String module, String day, Map<String, Object> map) {
		this.host = host;
		this.module = module;
		this.day = day;
		this.boltTime = System.currentTimeMillis();
		this.map = map;
	}

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
		return map;
	}

}
