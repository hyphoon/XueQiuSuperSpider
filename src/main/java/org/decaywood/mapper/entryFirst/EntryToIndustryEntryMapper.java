package org.decaywood.mapper.entryFirst;

import org.decaywood.entity.Entry;
import org.decaywood.entity.Industry;
import org.decaywood.entity.Stock;
import org.decaywood.mapper.AbstractMapper;
import org.decaywood.utils.EmptyObject;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * entry -> 板块信息
 */
public class EntryToIndustryEntryMapper extends AbstractMapper<Entry<Stock, Map<String, Object>>, Entry<Stock, Map<String, Object>>> {

    /**
     * key： 带 MACD 金叉天数的板块， value： 相关的股票代码
     * 通常用 XuQiuIndustryCollector 生成
     */
    private final Map<Industry, List<String>> industryMap;

    public EntryToIndustryEntryMapper(Map<Industry, List<String>> map) throws RemoteException {
        super(null);
        this.industryMap = map;
    }

    @Override
    public Entry<Stock, Map<String, Object>> mapLogic(Entry<Stock, Map<String, Object>> entry) throws Exception {
        Map<String, Object> map = entry.getValue();
        Stock stock = entry.getKey();
        if(stock == null || stock == EmptyObject.emptyStock)
            return entry;
        try {
            final String stockNo = stock.getStockNo();
            final StringBuilder sb = new StringBuilder();
            industryMap.keySet().stream()
                    .filter(industry -> industryMap.get(industry).contains(stockNo))
                    .forEach(industry -> sb.append(industry.getIndustryName()).append("(").append(industry.getIndustryInfo()).append(")").append(":").append(industry.getMacdCross() > 0 ? String.valueOf(industry.getMacdCross()): "-").append(" "));
            if (sb.length() > 0) {
                map.put(Entry.INDUSTRY_DESC, sb.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return entry;
    }
}
