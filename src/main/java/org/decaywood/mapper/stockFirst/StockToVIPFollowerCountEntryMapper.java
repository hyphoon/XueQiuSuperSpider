package org.decaywood.mapper.stockFirst;

import com.fasterxml.jackson.databind.JsonNode;
import org.decaywood.entity.Entry;
import org.decaywood.entity.Stock;
import org.decaywood.mapper.AbstractMapper;
import org.decaywood.timeWaitingStrategy.TimeWaitingStrategy;
import org.decaywood.utils.EmptyObject;
import org.decaywood.utils.URLMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author: decaywood
 * @date: 2015/11/30 16:39
 */

/**
 * 股票 -> 股票+雪球大V数量 映射器
 * 速度很慢，慎用
 */
public class StockToVIPFollowerCountEntryMapper extends AbstractMapper <Stock, Entry<Stock, Map<String, Integer>>> {

    /**
     * 值Map的key
     */
    public static final String VALUE_KEY = "vipCount";

    private static final String REQUEST_PREFIX = URLMapper.MAIN_PAGE + "/S/";
    private static final String REQUEST_SUFFIX = "/follows?page=";

    private static final Pattern PATTERN_A = Pattern.compile("^S[ZH][036].*$");

    private int VIPFriendsCountShreshold;
    private int latestK_NewFollowers;

    public StockToVIPFollowerCountEntryMapper() throws RemoteException {
        this(10000, 5);
    }

    public StockToVIPFollowerCountEntryMapper(int VIPFriendsCountShreshold, int latestK_NewFollowers) throws RemoteException {
        this(null, VIPFriendsCountShreshold, latestK_NewFollowers);
    }


    /**
     *
     * @param strategy 超时等待策略（null则设置为默认等待策略）
     * @param VIPFriendsCountShreshold 是否为大V的粉丝阈值（超过这个阈值视为大V）
     * @param latestK_NewFollowers 只将最近K个新增用户纳入计算范围
     */
    public StockToVIPFollowerCountEntryMapper(TimeWaitingStrategy strategy,
                                              int VIPFriendsCountShreshold,
                                              int latestK_NewFollowers) throws RemoteException {
        super(strategy);
        if(VIPFriendsCountShreshold < 0 || latestK_NewFollowers < 0) throw new IllegalArgumentException();
        this.VIPFriendsCountShreshold = VIPFriendsCountShreshold;
        this.latestK_NewFollowers = latestK_NewFollowers;
    }

    @Override
    public Entry<Stock, Map<String, Integer>> mapLogic(Stock stock) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put(VALUE_KEY, 0);
        if(stock == null || stock == EmptyObject.emptyStock)
            return new Entry<>(EmptyObject.emptyStock, map);

        String stockNo = stock.getStockNo();
        if(!PATTERN_A.matcher(stockNo).matches()) { // 并非A股
            return new Entry<>(EmptyObject.emptyStock, map);
        }

        int count = 0;
        for (int i = 1; i < latestK_NewFollowers; i++) {
            String reqUrl = REQUEST_PREFIX + stockNo + REQUEST_SUFFIX + i;
            URL url = new URL(reqUrl);
            String content = tryRequest(url, 10, s -> s != null && s.indexOf("follows=") > 0);
            try {
                JsonNode node = parseHtmlToJsonNode(content).get("followers");
                if (node.size() == 0) break;
                for (JsonNode jsonNode : node) {
                    int followersCount = jsonNode.get("followers_count").asInt();
                    if (followersCount > VIPFriendsCountShreshold) count++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("解析关注列表出错 " + url.toString());
            }
        }
        map.put(VALUE_KEY, count);
        return new Entry<>(stock, map);
    }


    private JsonNode parseHtmlToJsonNode(String content) throws IOException {
        Document doc = Jsoup.parse(content);
        String indexer1 = "follows=";
        String indexer2 = ";seajs.use";
        StringBuilder builder = new StringBuilder(
                doc.getElementsByTag("script")
                .get(15)
                .dataNodes()
                .get(0)
                .attr("data"));
        int index = builder.indexOf(indexer1);
        builder.delete(0, index + indexer1.length());
        index = builder.indexOf(indexer2);
        builder.delete(index, builder.length());
        return mapper.readTree(builder.toString());
    }
}
