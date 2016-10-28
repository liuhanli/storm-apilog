package cn.iot.log.api.bolt;

import java.util.Map;

/**
 * The Class AbstractBoltLogEvent.
 */
public abstract class AbstractBoltLogEvent {
	
	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public abstract String getHost();

	/**
	 * Gets the module.
	 *
	 * @return the module
	 */
	public abstract String getModule();

	/**
	 * Gets the day.
	 *
	 * @return the day
	 */
	public abstract String getDay();

	/**
	 * Gets the bolt time.
	 *
	 * @return the bolt time
	 */
	public abstract long getBoltTime();

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public abstract Map<String, Object> getMap();
}
