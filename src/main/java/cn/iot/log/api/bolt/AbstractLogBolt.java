package cn.iot.log.api.bolt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractLogBolt.
 */
public abstract class AbstractLogBolt extends BaseBasicBolt {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AbstractLogBolt.class);

	/** The local matcher. */
	private ThreadLocal<Matcher> localMatcher;

	/** The local keys. */
	private ThreadLocal<List<String>> localKeys;

	/** The regex. */
	private String regex;

	/** The out put field name. */
	private String[] outPutFieldNames;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.storm.topology.base.BaseBasicBolt#prepare(java.util.Map,
	 * org.apache.storm.task.TopologyContext)
	 */
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		localMatcher = new ThreadLocal<>();
		localKeys = new ThreadLocal<>();
		if (!isEmpty(regex)) {
			List<String> keyList = extractKeyList(regex);
			localKeys.set(keyList);
		}
	}

	/**
	 * 从正则表达式中提取出对应的关键字名称列表. 如：从 ：
	 * (?<time>^(?<day>\\d{4}-\\d{2}-\\d{2})\\s\\d{2}:\\d{2}:\\d{2},\\d{3})\\s\\d+\\s\\[(?<threadid>.+)\\]\\s(?<level>\\w+)\\s+(?<fullclass>[\\.
	 * \\w]+)\\s-\\s(?<message>.+) 该正则表达式中提取出： time,
	 * day,threadid,level,fullclass,message字段，并返回该字段的列表集合
	 * 
	 * @param regex
	 *            ：输入的正则表达式
	 * @return list: 包含该正则表达式中字段的列表集合
	 */
	private static List<String> extractKeyList(String regex) {
		List<String> list = new ArrayList<String>();
		Pattern pattern = Pattern.compile("\\?<(?<NAME>\\w+)>");
		Matcher matcher = pattern.matcher(regex);
		StringBuffer buff = new StringBuffer();
		while (matcher.find()) {
			String key = matcher.group("NAME");
			list.add(key);
			buff.append(key);
			buff.append("|");
		}
		logger.info("extractKeyList regex:{}, keys:{}", regex, buff.toString());
		return list;
	}

	/**
	 * Gets the keys.
	 *
	 * @return the keys
	 */
	public List<String> getKeys() {
		List<String> keys = localKeys.get();
		if (keys == null) {
			keys = extractKeyList(getRegex());
			localKeys.set(keys);
		}
		return keys;
	}

	/**
	 * Gets the matcher.
	 *
	 * @return the matcher
	 */
	public Matcher getMatcher() {
		Matcher matcher = localMatcher.get();
		if (matcher == null) {
			Pattern pattern = Pattern.compile(getRegex());
			matcher = pattern.matcher("");
			localMatcher.set(matcher);
		}
		return matcher;
	}

	/**
	 * Checks if is empty.
	 *
	 * @param str
	 *            the str
	 * @return true, if is empty
	 */
	public boolean isEmpty(String str) {
		if (str == null || str.length() < 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the regex.
	 *
	 * @return the regex
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * Sets the regex.
	 *
	 * @param regex
	 *            the new regex
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * Gets the out put field names.
	 *
	 * @return the out put field names
	 */
	public String[] getOutPutFieldNames() {
		return outPutFieldNames;
	}

	/**
	 * Sets the out put field names.
	 *
	 * @param outPutFieldNames the new out put field names
	 */
	public void setOutPutFieldNames(String... outPutFieldNames) {
		this.outPutFieldNames = outPutFieldNames;
	}
}
