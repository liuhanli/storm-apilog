package cn.iot.log.api.elasticsearch;

import java.util.Map;
import java.util.Random;

import org.apache.storm.tuple.Tuple;

import cn.iot.log.api.bolt.reqlog.ReqLogEvent;
import cn.iot.log.api.common.Constants;
import cn.iot.log.storm.elasticsearch.common.Document;
import cn.iot.log.storm.elasticsearch.mapper.TupleMapper;

public class ReqLogMapper implements TupleMapper<Document<Map<String, Object>>> {
    private static final long serialVersionUID = 1L;
    public static final String INDEX_NAME = "apiserver_req_log";
    public static final String INDEX_TYPE = "log";

    @Override
    public Document<Map<String, Object>> map(Tuple input) {
        String day = input.getStringByField(Constants.DAY);
        String transid = input.getStringByField(Constants.TRANSID);
        ReqLogEvent event = (ReqLogEvent) input.getValueByField(Constants.REQLOG);
        String id = String.format("%s#%d", transid, getRandom());
        String indexName = String.format("%s_%s", INDEX_NAME, day);
        String indexType = INDEX_TYPE;
        Document<Map<String, Object>> dataMap =
                new Document<Map<String, Object>>(indexName, indexType, event.getMap(), id);
        return dataMap;
    }

    public int getRandom() {
        Random random = new Random();
        int x = random.nextInt(899999);
        return x + 100000;
    }
}
