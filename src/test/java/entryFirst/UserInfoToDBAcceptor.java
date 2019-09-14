package entryFirst;

import org.decaywood.acceptor.AbstractAcceptor;
import org.decaywood.entity.Entry;
import org.decaywood.entity.Stock;
import org.decaywood.utils.DatabaseAccessor;
import org.decaywood.utils.EmptyObject;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Map;


/**
 * @author: decaywood
 * @date: 2015/12/1 13:27
 */

/**
 * 示例类， 接收信息放入数据库
 */
public class UserInfoToDBAcceptor extends AbstractAcceptor<Entry<Stock, Map<String, Object>>> {

    public UserInfoToDBAcceptor() throws RemoteException {}

    /**


     CREATE TABLE `xueqiuspider`.`stock_vip_followers` (
     `id` INT NOT NULL AUTO_INCREMENT,
     `stock_id` VARCHAR(45) NULL,
     `stock_name` VARCHAR(45) NULL,
     `vip_id` VARCHAR(45) NULL,
     `vip_count` INT NULL,
     'macd_cross' INT NULL,
     'industry_desc' VARCHAR(1000) NULL,
     PRIMARY KEY (`id`),
     UNIQUE INDEX `id_UNIQUE` (`id` ASC),
     UNIQUE INDEX `stock_id_UNIQUE` (`stock_id` ASC),
     UNIQUE INDEX `stock_name_UNIQUE` (`stock_name` ASC));

     */

    @Override
    protected void consumLogic(Entry<Stock, Map<String, Object>> entry) throws Exception{
        Stock stock = entry.getKey();
        if (stock != EmptyObject.emptyStock) {
            Map<String, Object> map = entry.getValue();
            Integer k = (Integer) map.get(Entry.VIP_COUNT_KEY);
            Integer m = (Integer) map.get(Entry.MACD_CROSS_KEY);
            String md = (String) map.get(Entry.INDUSTRY_DESC);
            Connection connection = DatabaseAccessor.Holder.ACCESSOR.getConnection();
            StringBuilder builder = new StringBuilder();
            builder.append("insert into stock_vip_followers ")
                    .append("(stock_id, stock_name, vip_count, macd_cross, industry_desc) ")
                    .append("values (?, ?, ?, ?, ?)")   // 1-5
                    .append("on duplicate key update vip_count=?, macd_cross=?, industry_desc=?");  // 6-8
            String sql = builder.toString();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, stock.getStockNo());
            statement.setString(2, stock.getStockName());
            statement.setInt(3, k);
            if (m == null) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, m);
            }
            statement.setString(5, md);
            statement.setInt(6, k);
            if (m == null) {
                statement.setNull(7, Types.INTEGER);
            } else {
                statement.setInt(7, m);
            }
            statement.setString(8, md);
            statement.execute();
            DatabaseAccessor.Holder.ACCESSOR.returnConnection(connection);
        }
    }
}
