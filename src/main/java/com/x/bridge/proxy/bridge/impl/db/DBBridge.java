package com.x.bridge.proxy.bridge.impl.db;

import com.google.auto.service.AutoService;
import com.pikachu.common.database.core.IDatabase;
import com.pikachu.framework.database.DaoManager;
import com.pikachu.framework.database.IDao;
import com.pikachu.framework.database.core.DatabaseConfig;
import com.pikachu.framework.database.core.ITableInfoGetter;
import com.pikachu.framework.database.core.PikachuTableInfoGetter;
import com.pikachu.framework.database.core.TableInfo;
import com.x.bridge.proxy.ProxyManager;
import com.x.bridge.proxy.bridge.core.BaseBridge;
import com.x.bridge.proxy.bridge.core.BridgeManager;
import com.x.bridge.proxy.bridge.core.IBridge;
import com.x.bridge.proxy.data.ChannelData;
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
public class DBBridge extends BaseBridge {

    private DBConfig config;

    private DaoManager daoManager;

    private ExecutorService reader;

    private ExecutorService writer;

    private ExecutorService deleter;

    private ITableInfoGetter<ChannelData> writeGetter;

    private ITableInfoGetter<ChannelData> readGetter;

    private TableInfo tableInfo;

    public DBBridge() {
        try {
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setName(config.getName());
            dbConfig.setUrl(config.getUrl());
            dbConfig.setDriver(config.getDriver());
            dbConfig.setUser(config.getUser());
            dbConfig.setPassword(config.getPassword());

            this.daoManager = new DaoManager(dbConfig);
            this.reader = Executors.newSingleThreadExecutor();
            this.writer = Executors.newSingleThreadExecutor();
            this.deleter = Executors.newSingleThreadExecutor();
            this.tableInfo = new PikachuTableInfoGetter<ChannelData>().getTableInfo(ChannelData.class);
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
    protected void onStart() throws Exception {
        reader.execute(new Runnable() {
            @Override
            public void run() {
                IDao<ChannelData> dao = daoManager.getDao(ChannelData.class, readGetter);
                while (true) {
                    try {
                        ChannelData[] datas = dao.getList(null, null);
                        if (datas != null && datas.length > 0) {
                            for (ChannelData data : datas) {
                                ProxyManager.receiveData(data);
                            }
                            deleteReaded(datas, daoManager, dao);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() throws Exception {
        reader.shutdown();
        daoManager.stop();
        BridgeManager.removeBridge(config.getName());
    }

    @Override
    public String name() {
        return "DB";
    }

    @Override
    public void send(ChannelData data) throws Exception {
        IDao<ChannelData> dao = daoManager.getDao(ChannelData.class, writeGetter);
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
    public IBridge<ChannelData> newInstance() {
        return new DBBridge();
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
