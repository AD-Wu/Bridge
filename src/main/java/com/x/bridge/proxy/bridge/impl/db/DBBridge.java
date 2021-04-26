package com.x.bridge.proxy.bridge.impl.db;

import com.google.auto.service.AutoService;
import com.pikachu.common.database.core.IDatabase;
import com.pikachu.framework.database.DaoManager;
import com.pikachu.framework.database.IDao;
import com.pikachu.framework.database.core.DatabaseConfig;
import com.pikachu.framework.database.core.ITableInfoGetter;
import com.pikachu.framework.database.core.PikachuTableInfoGetter;
import com.pikachu.framework.database.core.TableInfo;
import com.x.bridge.proxy.bridge.core.BridgeManager;
import com.x.bridge.proxy.bridge.core.IBridge;
import com.x.bridge.proxy.bridge.core.IReceiver;
import com.x.bridge.proxy.data.ChannelData;
import com.x.bridge.proxy.util.ProxyThreadFactory;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Desc 数据库桥
 * @Date 2020/11/13 17:36
 * @Author AD
 */
@Log4j2
@AutoService(IBridge.class)
public class DBBridge implements IBridge {

    private DBConfig config;

    private DaoManager daoManager;

    private ExecutorService reader;

    private ExecutorService writer;

    private ExecutorService deleter;

    private ITableInfoGetter<ChannelData> writeGetter;

    private ITableInfoGetter<ChannelData> readGetter;

    private TableInfo tableInfo;
    
    private List<IReceiver> receivers = new ArrayList<>();
   

    public DBBridge(IReceiver dataListener) {
        try {
            // 创建数据库访问对象管理者
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setName(config.getName());
            dbConfig.setUrl(config.getUrl());
            dbConfig.setDriver(config.getDriver());
            dbConfig.setUser(config.getUser());
            dbConfig.setPassword(config.getPassword());

            this.daoManager = new DaoManager(dbConfig);
            // 创建数据读取器
            this.reader = Executors.newSingleThreadExecutor(new ProxyThreadFactory("DB-Reader-"));
            // 数据发送器
            this.writer = Executors.newSingleThreadExecutor(new ProxyThreadFactory("DB-Writer-"));
            // 数据删除者
            this.deleter = Executors.newSingleThreadExecutor(new ProxyThreadFactory("DB-Deleter-"));
            this.tableInfo = new PikachuTableInfoGetter<ChannelData>().getTableInfo(ChannelData.class);
            // 写表信息获取器
            this.writeGetter = new ITableInfoGetter<ChannelData>() {
                @Override
                public TableInfo getTableInfo(Class<ChannelData> clazz) {
                    if (DBBridge.this.config.isOut()) {
                        tableInfo.setTableName(DBBridge.this.config.getOutWriteInTable());
                    } else {
                        tableInfo.setTableName(DBBridge.this.config.getInWriteOutTable());
                    }
                    return tableInfo;
                }
            };
            // 读表信息获取器
            this.readGetter = new ITableInfoGetter<ChannelData>() {
                @Override
                public TableInfo getTableInfo(Class<ChannelData> clazz) {
                    if (DBBridge.this.config.isOut()) {
                        tableInfo.setTableName(DBBridge.this.config.getInWriteOutTable());
                    } else {
                        tableInfo.setTableName(DBBridge.this.config.getOutWriteInTable());
                    }
                    return tableInfo;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String name() {
        return "DB";
    }
    
    @Override
    public void start() throws Exception {
        reader.execute(new Runnable() {
            @Override
            public void run() {
                IDao<ChannelData> dao = daoManager.getDao(ChannelData.class, readGetter);
                while (true) {
                    try {
                        // 读取所有数据
                        ChannelData[] datas = dao.getList(null, null);
                        // 判断数据是否有效
                        if (datas != null && datas.length > 0) {
                            // 异步删除已读数据
                            deleteReaded(datas, daoManager, dao);
                            // 回调读到的数据
                            for (IReceiver receiver : receivers) {
                                receiver.receive(datas);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void send(ChannelData data) throws Exception {
        IDao<ChannelData> dao = daoManager.getDao(ChannelData.class, writeGetter);
        // dao.add(data);
        writer.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    dao.add(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    @Override
    public void addReceiver(IReceiver receiver) {
        receivers.add(receiver);
    }
    
    @Override
    public void stop() throws Exception {
        reader.shutdown();
        daoManager.stop();
        BridgeManager.removeBridge(config.getName());
    }
    

    private void deleteReaded(ChannelData[] datas, DaoManager daoManager, IDao<ChannelData> dao) {
        deleter.execute(new Runnable() {
            @Override
            public void run() {
                // 获取数据库
                IDatabase db = daoManager.getDatabaseAccess();
                // 生成sql语句
                StringBuilder sql = new StringBuilder();
                sql.append("delete from ");
                sql.append(dao.getTableName());
                sql.append(" where ");
                String[] pks = dao.getPrimaryKeys();
                for (int i = 0, c = pks.length; i < pks.length; i++) {
                    if (i > 0) {
                        sql.append(" and ");
                    }
                    sql.append(pks[i]).append("=?");
                }
                // 获取参数
                List<Object[]> params = new ArrayList<>();
                for (ChannelData data : datas) {
                    params.add(dao.getPrimaryKeysValue(data));
                }
                try {
                    db.executeBatch(sql.toString(), params, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
