package org.decaywood.collector;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.time.DateUtils;
import org.decaywood.entity.Industry;
import org.decaywood.entity.trend.StockTrend;
import org.decaywood.timeWaitingStrategy.TimeWaitingStrategy;
import org.decaywood.utils.RequestParaBuilder;
import org.decaywood.utils.URLMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileNotFoundException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;

/**
 * @author: decaywood
 * @date: 2015/11/23 10:50
 */

/**
 * 板块收集器，收集雪球所有板块类型编号、名称、 MACD 金叉天数 以及 相关股票编码
 */
public class XuQiuIndustryCollector extends AbstractCollector<Map<Industry, List<String>>> {

    private static final String NO_PAGE_STR = "你访问的页面不存在，过会儿再来尝试嘛！";

    private static final String PAGE_STR = "stock-name";

    /**
     * MACD 的结束日期（倒序）
     */
    private Date to = DateUtils.truncate(new Date(), Calendar.DATE);

    /**
     * MACD 的数据数
     */
    private int count = 60;

    public XuQiuIndustryCollector() throws RemoteException {
        this(null);
    }

    /**
     *@param strategy 超时等待策略（null则设置为默认等待策略）
     */
    public XuQiuIndustryCollector(TimeWaitingStrategy strategy) throws RemoteException {
        super(strategy);
    }

    @Override
    public Map<Industry, List<String>> collectLogic() throws Exception {
        Map<Industry, List<String>> res = new HashMap<>();

        String target = URLMapper.COMPREHENSIVE_PAGE.toString();

        // 板块, 样例 https://xueqiu.com/S/BK0001,https://xueqiu.com/S/BK0501,https://xueqiu.com/S/BK0601,https://xueqiu.com/S/BK0901
        for (int i = 0; i < 10; i++) {  // 倒数第3位数字
            if (i == 1 || i== 2 || i==3 || i==4 || i==7 || i==8)
                continue;

            for (int j = 0; j < 100; j++) {    // 最后两位数字
                String jIdx = String.valueOf(j);
                if (j < 10) {
                    jIdx = "0" + jIdx;
                }
                // 读取板块
                Industry industry = readIndustry("0" + i + jIdx);
                if (industry == null) {
                    if (j > 0)
                        break;
                    else
                        continue;
                }
                // 读取MACD金叉日期
                readIndustryMacdCross(industry);
                // 读取相关股票编码
                List<String> stockNoList = readStock(industry);
                res.put(industry, stockNoList);
//                System.out.println("完成 " + "0" + i + jIdx);
            }
        }
        return res;
    }

    private Industry readIndustry(String no) {
        try {
            URL url = new URL(URLMapper.INDUSTRY_PAGE + no);
            String content = tryRequest(url, 10, s -> s.contains(NO_PAGE_STR) || s.contains(PAGE_STR));
            if (content.contains(NO_PAGE_STR)) {
                return null;
            }
            Document doc = Jsoup.parse(content);
            Element element = doc.getElementsByClass("stock-name").get(0);
            String industryStr = element.text().trim();
            String industryName = industryStr.substring(0, industryStr.indexOf("("));
            String industryNo = industryStr.substring(industryStr.indexOf(":") + 1, industryStr.indexOf(")"));
            return new Industry(industryName, industryNo);
        } catch (FileNotFoundException nfex) {
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void readIndustryMacdCross(Industry industry) {
        try {
            String target = URLMapper.STOCK_CHART_JSON.toString();
            RequestParaBuilder builder = new RequestParaBuilder(target)
                    .addParameter("symbol", industry.getIndustryInfo())
                    .addParameter("period", "day")
                    .addParameter("type", "before")
                    .addParameter("begin", to.getTime())
                    .addParameter("count", "-" + count)
                    .addParameter("indicator", "macd");

            URL url = new URL(builder.build());
            String json = tryRequest(url, 10, s -> s != null && s.indexOf("data") > 0);
            JsonNode node = mapper.readTree(json).get("data").get("item");
            List<StockTrend.TrendBlock> history = new ArrayList<>();
            for (JsonNode jsonNode : node) {
                String time = jsonNode.get(0).asText();
                String dif = jsonNode.get(1).asText();
                String dea = jsonNode.get(2).asText();
                String macd = jsonNode.get(3).asText();
                StockTrend.TrendBlock block = new StockTrend.TrendBlock(
                        null, null, null, null, null, null, null, null,
                        null, null, null, null, dif, dea, macd, time);
                history.add(block);
            }
            if (history.size() > 0) {
                Collections.reverse(history);
                int count = 0;
                for (StockTrend.TrendBlock block : history) {
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
                    industry.setMacdCross(count);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<String> readStock(Industry industry) {
        List<String> res = new ArrayList<>();
        try {
            String target = URLMapper.INDUSTRY_STOCK_JSON.toString();
            RequestParaBuilder builder = new RequestParaBuilder(target)
                    .addParameter("ind_code", industry.getIndustryInfo());
            URL url = new URL(builder.build());
            String json = tryRequest(url, 10, s -> s != null && s.indexOf("data") > 0);
            JsonNode node = mapper.readTree(json).get("data").get("items");
            for (JsonNode jsonNode : node) {
                res.add(jsonNode.get("symbol").asText());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }
}
