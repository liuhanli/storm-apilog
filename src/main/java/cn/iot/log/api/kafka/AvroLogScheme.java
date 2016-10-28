package cn.iot.log.api.kafka;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class AvroLogScheme implements Scheme {
	private static final Logger logger = LoggerFactory.getLogger(AvroLogScheme.class);
	private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
	public static final String AVRO_SCHEME_KEY = "avro";
	public static final String HOST = "host";
	public static final String MODULE = "module";
	public static final String BODY = "body";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public List<Object> deserialize(ByteBuffer buffer) {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes, 0, bytes.length);
		BinaryDecoder decoder = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			decoder = DecoderFactory.get().directBinaryDecoder(in, decoder);
			Optional<SpecificDatumReader<AvroLog>> reader = Optional.absent();
			if (!reader.isPresent()) {
				reader = Optional.of(new SpecificDatumReader<AvroLog>(AvroLog.class));
			}
			AvroLog event = reader.get().read(null, decoder);
			if (event == null) {
				return null;
			}
			Map<CharSequence, CharSequence> headers = event.getHeaders();
			LogEvent logEvent = new LogEvent();
			for (Map.Entry<CharSequence, CharSequence> entry : headers.entrySet()) {
				CharSequence key = entry.getKey();
				CharSequence value = entry.getValue();
				if (HOST.equalsIgnoreCase(key.toString())) {
					logEvent.setHost(value.toString());
				}
				if (MODULE.equalsIgnoreCase(key.toString())) {
					logEvent.setModule(value.toString());
				}
			}
			logEvent.setBody(new String(event.getBody().array(), UTF8_CHARSET));
			return new Values(logEvent);
		} catch (Exception e) {
			logger.error("AvroLogEventScheme deserialize exception", e);
		}
		return null;
	}

	@Override
	public Fields getOutputFields() {
		return new Fields(AVRO_SCHEME_KEY);
	}

}
