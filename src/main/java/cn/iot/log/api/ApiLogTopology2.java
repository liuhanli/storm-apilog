package cn.iot.log.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fhuss.storm.elasticsearch.ClientFactory;

import cn.iot.log.api.function.AvroLogParseFunction;

public class ApiLogTopology2 {
	private static final Logger logger = LoggerFactory.getLogger(ApiLogTopology2.class);

	public static void main(String[] args) throws Exception {
		ApiLogTopology2 toplogy = new ApiLogTopology2();
		toplogy.launchLocalCluster();
	}

	public void launchLocalCluster() throws Exception {
		LocalDRPC drpc = new LocalDRPC();
		LocalCluster cluster = new LocalCluster();

		TransactionalTridentKafkaSpout kafkaSpout = createKafkaSpout("192.168.156.60:2181/kafka",
				"apiserver_log_dev", "log-client");

		Config conf = new Config();
		conf.setMaxSpoutPending(200);
		conf.put("storm.elasticsearch.cluster.name", "LOG-CLUSTER");
		conf.put("storm.elasticsearch.hosts", "192.168.156.60:9300");

		TridentTopology topology = new TridentTopology();

		topology.newStream("api_log", kafkaSpout).shuffle().each(new Fields("str"),
				new AvroLogParseFunction(), new Fields("event"));
		cluster.submitTopology("stormApiLog", conf, topology.build());
	}

	private TransactionalTridentKafkaSpout createKafkaSpout(String zkUrl, String topic,
			String clientId) {
		ZkHosts hosts = new ZkHosts(zkUrl);
		TridentKafkaConfig config = new TridentKafkaConfig(hosts, topic, clientId);
		config.scheme = new SchemeAsMultiScheme(new StringScheme());
		// Consume new data from the topic
		config.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
		return new TransactionalTridentKafkaSpout(config);
	}

	/*
	 * private IndexBatchBolt createESBolt() { DefaultTupleMapper mapper =
	 * DefaultTupleMapper.newObjectDefaultTupleMapper(); return new
	 * IndexBatchBolt<>(getClient(), mapper, 5, TimeUnit.SECONDS); }
	 */

	public ClientFactory.Transport getClient() {
		Map<String, String> settings = new HashMap<>();
		settings.put("client.transport.sniff", "true");
		settings.put("node.name", "storm-es-client");
		settings.put("transport.netty.connect_timeout", "10000ms");
		return new ClientFactory.Transport(settings);
	}

}
