package collectTest;

import org.decaywood.collector.XuQiuIndustryCollector;
import org.decaywood.entity.Industry;
import org.junit.Test;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class XuQiuIndustryCollectorTest {

    @Test
    public void collectLogic() throws RemoteException {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        XuQiuIndustryCollector collector = new XuQiuIndustryCollector();
        collector.anonymous();
        Map<Industry, List<String>> map = collector.get();
        for (Industry industry : map.keySet()) {
            String s = map.get(industry).stream().collect(Collectors.joining(" "));
            System.out.println(industry.getIndustryName() + " " + industry.getIndustryInfo() + " " + industry.getMacdCross() + " " + s);
        }
    }
}
