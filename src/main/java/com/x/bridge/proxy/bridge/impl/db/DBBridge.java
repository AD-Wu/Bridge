package com.x.bridge.proxy.bridge.impl.db;

import com.google.auto.service.AutoService;
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

    private ExecutorService thread;

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
            this.thread = Executors.newSingleThreadExecutor();
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
        thread.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    IDao<ChannelData> dao = daoManager.getDao(ChannelData.class, readGetter);
                    try {
                        ChannelData[] datas = dao.getList(null, null);
                        for (ChannelData data : datas) {
                            ProxyManager.receiveData(data);
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
        thread.shutdown();
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
        dao.add(data);
    }

    @Override
    public IBridge<ChannelData> newInstance() {
        return new DBBridge();
    }


}
