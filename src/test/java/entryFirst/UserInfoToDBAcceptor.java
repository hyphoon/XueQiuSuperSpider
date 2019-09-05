package entryFirst;

import org.decaywood.acceptor.AbstractAcceptor;
import org.decaywood.entity.Entry;
import org.decaywood.entity.Stock;
import org.decaywood.mapper.entryFirst.EntryToMacdCrossEntryMapper;
import org.decaywood.mapper.stockFirst.StockToVIPFollowerCountEntryMapper;
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
public class UserInfoToDBAcceptor extends AbstractAcceptor<Entry<Stock, Map<String, Integer>>> {

    public UserInfoToDBAcceptor() throws RemoteException {}

    /**


     CREATE TABLE `xueqiuspider`.`stock_vip_followers` (
     `id` INT NOT NULL AUTO_INCREMENT,
     `stock_id` VARCHAR(45) NULL,
     `stock_name` VARCHAR(45) NULL,
     `vip_id` VARCHAR(45) NULL,
     `vip_count` INT NULL,
     'macd_cross' INT NULL,
     PRIMARY KEY (`id`),
     UNIQUE INDEX `id_UNIQUE` (`id` ASC),
     UNIQUE INDEX `stock_id_UNIQUE` (`stock_id` ASC),
     UNIQUE INDEX `stock_name_UNIQUE` (`stock_name` ASC));

     */

    @Override
    protected void consumLogic(Entry<Stock, Map<String, Integer>> entry) throws Exception{
        Stock stock = entry.getKey();
        if (stock != EmptyObject.emptyStock) {
            Map<String, Integer> map = entry.getValue();
            Integer k = map.get(StockToVIPFollowerCountEntryMapper.VALUE_KEY);
            Integer m = map.get(EntryToMacdCrossEntryMapper.VALUE_KEY);
            Connection connection = DatabaseAccessor.Holder.ACCESSOR.getConnection();
            StringBuilder builder = new StringBuilder();
            builder.append("insert into stock_vip_followers ")
                    .append("(stock_id, stock_name, vip_count, macd_cross) ")
                    .append("values (?, ?, ?, ?)")
                    .append("on duplicate key update vip_count=?, macd_cross=?");
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
            statement.setInt(5, k);
            if (m == null) {
                statement.setNull(6, Types.INTEGER);
            } else {
                statement.setInt(6, m);
            }
            statement.execute();
            DatabaseAccessor.Holder.ACCESSOR.returnConnection(connection);
        }
    }
}
