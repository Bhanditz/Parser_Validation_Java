package org.bibalex.eol.handlers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

/**
 * @author Mina.Aiad
 */
public class HbaseHandler {


    private static HbaseHandler instance = null;
    private Configuration config;
    private Connection connection;
    private Admin admin;

    private HbaseHandler(String configFilePath) {
        config = HBaseConfiguration.create();
        config.addResource(new Path(configFilePath));
        try {
            admin = connection.getAdmin();
            connection = ConnectionFactory.createConnection(config);

        } catch (IOException e) {
            System.err.println("Failed to initailze connection with HBase as an admin." + e);
        }
    }

    public static HbaseHandler getHbaseHandler(String configFilePath) {

        if (instance == null) {
            instance = new HbaseHandler(configFilePath);
        }
        return instance;
    }

    public boolean CreateTable(String tableName, String[] columFamilies) {
        try {
            HTableDescriptor table =
                    new HTableDescriptor(TableName.valueOf(tableName));
            for (int i = 0; i < columFamilies.length; i++)
                table.addFamily(new HColumnDescriptor(columFamilies[i]));

            if (!admin.tableExists(table.getTableName())) {
                admin.createTable(table);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Failed to create Table on HBase " + e);
        } finally {
            return false;
        }

    }

    public boolean DropTable(String tableName) {
        try {
            HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
            if (admin.tableExists(table.getTableName())) {
                admin.disableTable(table.getTableName());
                admin.deleteTable(table.getTableName());
            }
            return true;
        } catch (IOException e) {
            System.err.println("Failed to drop table " + tableName + " from HBase " + e);
        } finally {
            return false;
        }
    }

    public boolean addRow(String tableName, byte[] columFamily, byte[] rowKey, byte[] columName,
                          byte[] value) {
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            // Construct a "put" object for insert
            Put p = new Put(rowKey);
            p.addColumn(columFamily, columName, value);
            table.put(p);
            table.close();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to add row to " + tableName + " Table on HBase " + e);
        } finally {
            return false;
        }
    }

    public Result getRow(String tableName, byte[] rowKey) {

        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get g = new Get(rowKey);
            // Get the result by passing the getter to the table
            Result r = table.get(g);
            table.close();
            return r;
        } catch (IOException e) {
            System.err.println("Failed to add row to " + tableName + " Table on HBase: " + e);
        }
        return null;

    }

    public ResultScanner scan(String tableName, byte[] startRowkey, byte[] stopRowKey) {
        try {

            ResultScanner results = null;
            Table table = connection.getTable(TableName.valueOf(tableName));
            // Create the scan
            Scan scan = new Scan();
            // start at a specific rowkey.
            if (startRowkey != null)
                scan.setStartRow(startRowkey);
            if (stopRowKey != null)
                scan.setStopRow(stopRowKey);
            // Tell the server not to cache more than limit rows
            // since we won;t need them
            // scan.setCaching(limit);
            // Can also set a server side filter
            //  scan.setFilter(new PageFilter(limit));
            // Get the scan results
            results = table.getScanner(scan);

            return results;
        } catch (IOException e) {
            System.err.println("Failed to add row to " + tableName + " Table on HBase " + e);
        }
        return null;
    }

    public boolean release() {
        try {
            connection.close();
            admin.close();
            instance = null;
            return true;
        } catch (IOException e) {
            System.err.println("Failed to relase Hbase Handler ");
        } finally {
            return false;
        }
    }

}
