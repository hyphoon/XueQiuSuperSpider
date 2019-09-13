package org.decaywood.mapper.entryFirst;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.time.DateUtils;
import org.decaywood.entity.Entry;
import org.decaywood.entity.Stock;
import org.decaywood.entity.trend.StockTrend;
import org.decaywood.mapper.AbstractMapper;
import org.decaywood.timeWaitingStrategy.TimeWaitingStrategy;
import org.decaywood.utils.EmptyObject;
import org.decaywood.utils.RequestParaBuilder;
import org.decaywood.utils.URLMapper;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;

/**
 * 股票 -> 最近一次Macd金叉的周期
 */
public class EntryToMacdCrossEntryMapper extends AbstractMapper<Entry<Stock, Map<String, Object>>, Entry<Stock, Map<String, Object>>> {

    private int count;
    private Date to;

    public EntryToMacdCrossEntryMapper() throws RemoteException {
        this(null, 60, null);
    }

    public EntryToMacdCrossEntryMapper(Date to) throws RemoteException {
        this(null, 60, to);
    }

    public EntryToMacdCrossEntryMapper(TimeWaitingStrategy strategy, int count, Date to) throws RemoteException {
        super(strategy);
        this.count = count;
        if (to == null) {
            this.to = DateUtils.truncate(new Date(), Calendar.DATE);
        } else {
            this.to = DateUtils.truncate(to, Calendar.DATE);
        }
    }

    @Override
    public Entry<Stock, Map<String, Object>> mapLogic(Entry<Stock, Map<String, Object>> entry) throws Exception {
        Map<String, Object> map = entry.getValue();
        map.put(Entry.MACD_CROSS_KEY, null);
        Stock stock = entry.getKey();
        if(stock == null || stock == EmptyObject.emptyStock)
            return entry;
        try {
            StockTrend trend = stock.getStockTrend();
            if (trend == null || trend == EmptyObject.emptyStockTrend) {
                String target = URLMapper.STOCK_CHART_JSON.toString();
                RequestParaBuilder builder = new RequestParaBuilder(target)
                        .addParameter("symbol", stock.getStockNo())
                        .addParameter("period", "day")
                        .addParameter("type", "before")
                        .addParameter("begin", to.getTime())
                        .addParameter("count", "-" + count)
                        .addParameter("indicator", "macd");

                URL url = new URL(builder.build());
                String json = tryRequest(url, 10, s -> s != null && s.indexOf("data") > 0);
                JsonNode node = mapper.readTree(json).get("data").get("item");
                processStock(stock, node);
                trend = stock.getStockTrend();
            }

            if (trend != EmptyObject.emptyStockTrend && trend.getHistory() != null && trend.getHistory().size() > 0) {
                List<StockTrend.TrendBlock> historyReverse = new ArrayList<>(trend.getHistory());
                Collections.reverse(historyReverse);
                int count = 0;
                for (StockTrend.TrendBlock block : historyReverse) {
                    String macd = block.getMacd();
                    if (macd == null) {
                        break;
                    }
                    Double d = Double.valueOf(macd);
                    if (d < 0) {
                        break;
                    }
                    count++;
                }
                if (count > 0) {
                    map.put(Entry.MACD_CROSS_KEY, count);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("读取MACD过程出错 " + stock.getStockName());
        }
        return entry;
    }

    private void processStock(Stock stock, JsonNode node) {
        List<StockTrend.TrendBlock> history = new ArrayList<>();
        for (JsonNode jsonNode : node) {
//            String volume = jsonNode.get("volume").asText();
//            String open = jsonNode.get("open").asText();
//            String high = jsonNode.get("high").asText();
//            String close = jsonNode.get("close").asText();
//            String low = jsonNode.get("low").asText();
//            String chg = jsonNode.get("chg").asText();
//            String percent = jsonNode.get("percent").asText();
//            String turnrate = jsonNode.get("turnrate").asText();
//            String ma5 = jsonNode.get("ma5").asText();
//            String ma10 = jsonNode.get("ma10").asText();
//            String ma20 = jsonNode.get("ma20").asText();
//            String ma30 = jsonNode.get("ma30").asText();
            String time = jsonNode.get(0).asText();
            String dif = jsonNode.get(1).asText();
            String dea = jsonNode.get(2).asText();
            String macd = jsonNode.get(3).asText();

//            StockTrend.TrendBlock block = new StockTrend.TrendBlock(
//                    volume, open, high, close, low, chg, percent, turnrate,
//                    ma5, ma10, ma20, ma30, dif, dea, macd, time);
            StockTrend.TrendBlock block = new StockTrend.TrendBlock(
                    null, null, null, null, null, null, null, null,
                    null, null, null, null, dif, dea, macd, time);
            history.add(block);
        }
        StockTrend trend = history.isEmpty() ? EmptyObject.emptyStockTrend
                : new StockTrend(stock.getStockNo(), StockTrend.Period.DAY, history);
        stock.setStockTrend(trend);
    }
}
