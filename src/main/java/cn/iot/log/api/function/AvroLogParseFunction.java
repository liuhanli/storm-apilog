package cn.iot.log.api.function;

import java.io.IOException;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvroLogParseFunction extends BaseFunction {
	private static final Logger logger = LoggerFactory.getLogger(AvroLogParseFunction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Schema schema;

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		try {
//			Message message = new Message(tuple.getString(0).getBytes("UTF-8"));
//			ByteBuffer payload = message.payload();
//			byte[] bytesMessage = new byte[payload.limit()];
//			payload.get(bytesMessage);
//			BinaryDecoder decoder = null;
//			ByteArrayInputStream in = new ByteArrayInputStream(bytesMessage);
//			decoder = DecoderFactory.get().directBinaryDecoder(in, decoder);
//			Optional<SpecificDatumReader<AvroLogEvent>> reader = Optional.absent();
//			if (!reader.isPresent()) {
//				reader = Optional.of(new SpecificDatumReader<AvroLogEvent>(AvroLogEvent.class));
//			}
//			AvroLogEvent event = reader.get().read(null, decoder);
//			String messageStr = new String(event.getBody().array(), "UTF-8");
		}catch (Exception e) {
			logger.error("error",e);
		}
	}

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		Schema.Parser parser = new Schema.Parser();
		try {
			schema = parser.parse(getClass().getResourceAsStream("/AvroLogEvent.avsc"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void cleanup() {
		super.cleanup();
	}

}
