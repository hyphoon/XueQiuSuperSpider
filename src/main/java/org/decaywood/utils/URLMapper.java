package org.decaywood.utils;

/**
 * @author: decaywood
 * @date: 2015/11/24 18:54
 */
public enum URLMapper {

    /*--------------------------------  Xue Qiu     --------------------------------------*/

    MAIN_PAGE("https://xueqiu.com"),
    COMPREHENSIVE_PAGE("https://xueqiu.com/hq"),

    HU_SHEN_NEWS_REF_JSON("https://xueqiu.com/statuses/topic.json"),
    STOCK_SHAREHOLDERS_JSON("https://xueqiu.com/stock/f10/shareholdernum.json"),
    STOCK_SELECTOR_JSON("https://xueqiu.com/stock/screener/screen.json"),
    LONGHUBANG_JSON("https://xueqiu.com/stock/f10/bizunittrdinfo.json"),
    STOCK_INDUSTRY_JSON("https://xueqiu.com/stock/f10/compinfo.json"),
    CUBE_REBALANCING_JSON("https://xueqiu.com/cubes/rebalancing/history.json"),
    CUBE_TREND_JSON("https://xueqiu.com/cubes/nav_daily/all.json"),
    CUBES_RANK_JSON("https://xueqiu.com/cubes/discover/rank/cube/list.json"),
    MARKET_QUOTATIONS_RANK_JSON("https://xueqiu.com/stock/quote_order.json"),
    SCOPE_STOCK_RANK_JSON("https://xueqiu.com/stock/rank.json"),
    STOCK_TREND_JSON("https://xueqiu.com/stock/forchartk/stocklist.json"),
    STOCK_JSON("https://xueqiu.com/v4/stock/quote.json"),
//    INDUSTRY_JSON("https://xueqiu.com/industry/quote_order.json"),
    INDUSTRY_JSON("https://xueqiu.com/service/v5/stock/screener/quote/list"),
    /*--------------------------------  NetEase     --------------------------------------*/

    NETEASE_MAIN_PAGE("https://quotes.money.163.com/stock"),
    STOCK_CAPITAL_FLOW("https://quotes.money.163.com/service/zjlx_chart.html"),

    /*-------------------------------- Leo -----------------------------------*/
    // 个股K线数据， 样例： https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH600028&begin=1567676507378&period=day&type=before&count=-142&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance
    STOCK_CHART_JSON("https://stock.xueqiu.com/v5/stock/chart/kline.json"),

    // 板块, 样例 https://xueqiu.com/S/BK0001,https://xueqiu.com/S/BK0501,https://xueqiu.com/S/BK0601,https://xueqiu.com/S/BK0901
    INDUSTRY_PAGE("https://xueqiu.com/S/BK"),

    // 板块相关个股， 样例 https://stock.xueqiu.com/v5/stock/forum/stocks.json?ind_code=BK0001
    INDUSTRY_STOCK_JSON("https://stock.xueqiu.com/v5/stock/forum/stocks.json"),
    ;


    URLMapper(String URL) {
        this.URL = URL;
    }

    private String URL;

    @Override
    public String toString() {
        return URL;
    }
}
