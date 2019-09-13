package org.decaywood.collector;

import org.decaywood.entity.Industry;
import org.decaywood.timeWaitingStrategy.TimeWaitingStrategy;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * @author: decaywood
 * @date: 2015/11/23 10:50
 */

/**
 * 板块收集器，收集雪球所有板块类型编号、名称、 MACD 金叉天数 以及 相关股票编码
 */
public class XuQiuIndustryCollector extends AbstractCollector<Map<Industry, List<String>>> {

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

//        List<Industry> res = new ArrayList<>();
//
//        String target = URLMapper.COMPREHENSIVE_PAGE.toString();
//
//        String content = request(new URL(target));
//        Document doc = Jsoup.parse(content);
//        Elements element = doc.getElementsByClass("second-nav")
//                .get(1).children()
//                .get(2).children()
//                .get(3).children()
//                .select("a");
//        StringBuilder builder = new StringBuilder();
//        for (Element ele : element) {
//            if (!ele.hasAttr("title") || !ele.hasAttr("href")) continue;
//            builder.append(ele.attr("href"));
//            res.add(new Industry(ele.attr("title"), builder.toString()));
//            builder.delete(0, builder.length());
//        }
//
//        return res;
        return null;
    }



}
