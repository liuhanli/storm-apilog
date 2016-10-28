package cn.iot.log.api;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.iot.log.api.bolt.translog.TransLogBolt;
import cn.iot.log.api.kafka.AvroLogScheme;

public class ApiLogTopology1 {
	private static final Logger logger = LoggerFactory.getLogger(ApiLogTopology1.class);

	public static void main(String[] args) throws Exception {
		ApiLogTopology1 toplogy = new ApiLogTopology1();
		toplogy.launchLocalCluster();
	}

	public void launchLocalCluster() throws Exception {
		logger.info("begin to start local topology.......");
		KafkaSpout kafkaSpout = createKafkaSpout();
		TransLogBolt transLogBolt = createTransLogBolt();
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", kafkaSpout, 4);
		builder.setBolt("translog", transLogBolt, 10).shuffleGrouping("spout");
		// builder.setBolt("printlog", new PrintLogBolt(),
		// 1).shuffleGrouping("translog");
		StormTopology topology = builder.createTopology();
		startLocalTopology(topology);
		logger.info("finish start local topology");
	}

	private void startLocalTopology(StormTopology topology) throws Exception {
		Config config = new Config();
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("storm-log", config, topology);
		Thread.sleep(60000);
		cluster.shutdown();
	}

	private TransLogBolt createTransLogBolt() {
		TransLogBolt bolt = new TransLogBolt();
		bolt.setRegex("(?<endtime>(?<day>^\\d{4}-\\d{2}-\\d{2})\\s\\d{2}:\\d{2}:\\d{2},\\d{3})\\s\\d+\\s\\[[\\w-]+\\]\\s(?<level>\\w+)\\s+[\\. \\w]+\\s-\\s+\\[(?<transid>[\\w]+)\\].+");
		return bolt;
	}

	private KafkaSpout createKafkaSpout() {
		ZkHosts hosts = new ZkHosts("192.168.156.60:2181", "/kafka/brokers");
		SpoutConfig spoutConfig = new SpoutConfig(hosts, "apiserver_log_dev", "/storm-log",
				"1365055");
		spoutConfig.scheme = new SchemeAsMultiScheme(new AvroLogScheme());
		spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
		return new KafkaSpout(spoutConfig);
	}
}
