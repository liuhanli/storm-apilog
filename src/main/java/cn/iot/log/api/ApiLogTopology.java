package cn.iot.log.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.iot.log.api.bolt.print.PrintLogBolt;
import cn.iot.log.api.bolt.reqlog.ReqLogBolt;
import cn.iot.log.api.bolt.reqlog.end.EndReqLogBolt;
import cn.iot.log.api.bolt.reqlog.start.StartReqLogBolt;
import cn.iot.log.api.bolt.translog.TransLogBolt;
import cn.iot.log.api.common.Constants;
import cn.iot.log.api.elasticsearch.ReqLogMapper;
import cn.iot.log.api.kafka.AvroLogScheme;
import cn.iot.log.storm.elasticsearch.bolt.IndexBatchBolt;
import cn.iot.log.storm.elasticsearch.common.ClientFactory;
import cn.iot.log.storm.elasticsearch.mapper.TupleMapper;

public class ApiLogTopology {
    private static final Logger logger = LoggerFactory.getLogger(ApiLogTopology.class);

    public static void main(String[] args) throws Exception {
        ApiLogTopology toplogy = new ApiLogTopology();
        toplogy.startLocalCluster();
    }

    private void startLocalCluster() throws Exception {
        Config config = new Config();
        config.put("storm.elasticsearch.cluster.name", "LOG-CLUSTER");
        config.put("storm.elasticsearch.hosts", "192.168.156.50:9300,192.168.156.51:9300");
        LocalCluster cluster = new LocalCluster();
        StormTopology topology = createTopology();
        cluster.submitTopology("storm-log", config, topology);
        Thread.sleep(66660000);
        cluster.shutdown();
    }

    private void startCluster() throws Exception {
        logger.info("Begin to start cluster topology.......");
        Config config = new Config();
        config.put(Config.NIMBUS_SEEDS, parseNimbus("192.168.156.60"));
        config.put(Config.NIMBUS_THRIFT_PORT, Integer.parseInt("6627"));
        config.put(Config.STORM_ZOOKEEPER_PORT, parseZkPort("192.168.156.58:2181,192.168.156.60:2181"));
        config.put(Config.STORM_ZOOKEEPER_SERVERS, parseZkHosts("192.168.156.58:2181,192.168.156.60:2181"));

        config.put("storm.elasticsearch.cluster.name", "LOG-CLUSTER");
        config.put("storm.elasticsearch.hosts", "192.168.156.58:9300,192.168.156.60:9300");
        config.setNumWorkers(10);
        config.setMaxSpoutPending(5000);
        StormTopology topology = createTopology();
        StormSubmitter.submitTopology("storm-log", config, topology);
        logger.info("Finish start cluster topology");
    }

    private static List<String> parseNimbus(String nimbus) {
        String[] hostsAndPorts = nimbus.split(",");
        List<String> hosts = new ArrayList<String>(hostsAndPorts.length);
        for (int i = 0; i < hostsAndPorts.length; i++) {
            hosts.add(i, hostsAndPorts[i].split(":")[0]);
        }
        return hosts;
    }

    private static List<String> parseZkHosts(String zkNodes) {
        String[] hostsAndPorts = zkNodes.split(",");
        List<String> hosts = new ArrayList<String>(hostsAndPorts.length);
        for (int i = 0; i < hostsAndPorts.length; i++) {
            hosts.add(i, hostsAndPorts[i].split(":")[0]);
        }
        return hosts;
    }

    private static int parseZkPort(String zkNodes) {
        String[] hostsAndPorts = zkNodes.split(",");
        int port = Integer.parseInt(hostsAndPorts[0].split(":")[1]);
        return port;
    }

    private StormTopology createTopology() throws Exception {
        logger.info("begin to create topology.......");
        KafkaSpout kafkaSpout = createKafkaSpout();
        TransLogBolt transLogBolt = createTransLogBolt();
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(Constants.KAFKASPOUT, kafkaSpout, 4);

        // 交易日志
        builder.setBolt(Constants.TRANSLOG, transLogBolt, 10).shuffleGrouping(Constants.KAFKASPOUT);
        // builder.setBolt(Constants.PRINTLOG + 1, new PrintLogBolt(), 4)
        // .fieldsGrouping(Constants.TRANSLOG, new Fields(Constants.HOST));

        // 请求日志
        StartReqLogBolt startReqLogBolt = createStartReqLogBolt();
        EndReqLogBolt endReqLogBolt = createEndReqLogBolt();
        ReqLogBolt reqLogBolt = createReqLogBolt();
        PrintLogBolt printLogBolt = createPrintLogBolt();
        builder.setBolt(Constants.STARTREQLOG, startReqLogBolt, 10).shuffleGrouping(Constants.KAFKASPOUT);
        builder.setBolt(Constants.ENDREQLOG, endReqLogBolt, 10).shuffleGrouping(Constants.KAFKASPOUT);
        builder.setBolt(Constants.REQLOG, reqLogBolt, 10)
                .fieldsGrouping(Constants.STARTREQLOG, new Fields(Constants.HOST, Constants.TRANSID))
                .fieldsGrouping(Constants.ENDREQLOG, new Fields(Constants.HOST, Constants.TRANSID));
//        builder.setBolt(Constants.PRINTLOG + 2, printLogBolt, 4).fieldsGrouping(Constants.REQLOG,
//                new Fields(Constants.HOST));

        // 添加ES入库Bolt
        IndexBatchBolt<Map<String, Object>> indexBatchBolt = createIndexBatchBolt();
        builder.setBolt(Constants.INDEXBATCH, indexBatchBolt, 4).shuffleGrouping(Constants.REQLOG);
        
        StormTopology topology = builder.createTopology();
        return topology;
    }

    private TransLogBolt createTransLogBolt() {
        TransLogBolt bolt = new TransLogBolt();
        bolt.setRegex(
                "(?<time>(?<day>^\\d{4}-\\d{2}-\\d{2})\\s\\d{2}:\\d{2}:\\d{2},\\d{3})\\s\\d+\\s\\[[\\w-]+\\]\\s(?<level>\\w+)\\s+[\\. \\w]+\\s-\\s+\\[(?<transid>[\\w]+)\\].+");
        bolt.setOutPutFieldNames(Constants.HOST, Constants.MODULE, Constants.DAY, Constants.TRANSID,
                Constants.TRANSLOG);
        return bolt;
    }

    private StartReqLogBolt createStartReqLogBolt() {
        StartReqLogBolt bolt = new StartReqLogBolt();
        bolt.setRegex(
                "(?<starttime>(?<day>^\\d{4}-\\d{2}-\\d{2})\\s\\d{2}:\\d{2}:\\d{2},\\d{3})\\s\\d+\\s\\[[\\w-]+\\]\\s\\w+\\s+[\\. \\w]+\\s-\\s+\\[(?<transid>.+)\\]\\sbegin to request,appid:(?<appid>\\w+),ebid:(?<ebid>\\w+),ip:(?<ip>[\\d.]+),params:(?<params>.+)");
        bolt.setOutPutFieldNames(Constants.HOST, Constants.TRANSID, Constants.STARTREQLOG);
        return bolt;
    }

    private EndReqLogBolt createEndReqLogBolt() {
        EndReqLogBolt bolt = new EndReqLogBolt();
        bolt.setRegex(
                "(?<endtime>(?<day>^\\d{4}-\\d{2}-\\d{2})\\s\\d{2}:\\d{2}:\\d{2},\\d{3})\\s\\d+\\s\\[[\\w-]+\\]\\s\\w+\\s+[\\. \\w]+\\s-\\s+\\[(?<transid>.+)\\]end callAPIServer\\[(?<appname>\\w+)\\],status:(?<status>\\d+),time-consuming:(?<costtime>\\d+)");
        bolt.setOutPutFieldNames(Constants.HOST, Constants.TRANSID, Constants.ENDREQLOG);
        return bolt;
    }

    private ReqLogBolt createReqLogBolt() {
        ReqLogBolt bolt = new ReqLogBolt();
        bolt.setOutPutFieldNames(Constants.HOST, Constants.MODULE, Constants.DAY, Constants.TRANSID, Constants.REQLOG);
        return bolt;
    }

    private PrintLogBolt createPrintLogBolt() {
        PrintLogBolt bolt = new PrintLogBolt();
        return bolt;
    }

    private KafkaSpout createKafkaSpout() {
        ZkHosts hosts = new ZkHosts("192.168.156.60:2181", "/kafka/brokers");
        SpoutConfig spoutConfig = new SpoutConfig(hosts, "apiserver_log_dev", "/storm-log", "1365055");
        spoutConfig.scheme = new SchemeAsMultiScheme(new AvroLogScheme());
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        return new KafkaSpout(spoutConfig);
    }

    private IndexBatchBolt<Map<String, Object>> createIndexBatchBolt() {
        ClientFactory clientFactory = getClient();
        TupleMapper mapper = new ReqLogMapper();
        IndexBatchBolt bolt = new IndexBatchBolt<>(5, TimeUnit.SECONDS);
        bolt.setClientFactory(clientFactory);
        bolt.setMapper(mapper);
        return bolt;
    }

    public ClientFactory getClient() {
        Map<String, String> settting = new HashMap<>();
        settting.put("transport.netty.connect_timeout", "10s");
        return new ClientFactory.TransportClientFactory(settting);
    }

}
