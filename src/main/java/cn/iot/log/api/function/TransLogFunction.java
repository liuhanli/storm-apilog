package cn.iot.log.api.function;

import java.io.IOException;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransLogFunction extends BaseFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(TransLogFunction.class);
	private Schema schema;
	
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String sourceLog = tuple.getString(0);
		logger.info("execute source log:{}", sourceLog);
		
		collector.emit(new Values(sourceLog));
	}

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		Schema.Parser parser = new Schema.Parser();
		Schema schema;
		try {
			schema = parser.parse(getClass().getResourceAsStream("/LogEvent.avsc"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		super.prepare(conf, context);
	}

	@Override
	public void cleanup() {
		super.cleanup();
	}
	
	

}
