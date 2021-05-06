package com.x.bridge.proxy.bridge;

import com.pikachu.common.database.core.IDatabase;
import com.pikachu.framework.database.DaoManager;
import com.pikachu.framework.database.IDao;
import com.pikachu.framework.database.core.DatabaseConfig;
import com.pikachu.framework.database.core.ITableInfoGetter;
import com.pikachu.framework.database.core.PikachuTableInfoGetter;
import com.pikachu.framework.database.core.TableInfo;
import com.x.bridge.common.IReceiver;
import com.x.bridge.common.ISender;
import com.x.bridge.data.ChannelData;
import com.x.bridge.data.DBConfig;
import com.x.bridge.data.ProxyThreadFactory;
import com.x.doraemon.util.ArrayHelper;
import com.x.doraemon.util.StringHelper;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Desc 数据库桥
 * @Date 2020/11/13 17:36
 * @Author AD
 */
@Log4j2
public class DBSender implements ISender<ChannelData> {
    
    private final DBConfig config;
    private DaoManager daoManager;
    private ScheduledExecutorService reader;
    private ExecutorService writer;
    private ExecutorService deleter;
    private ExecutorService callbacker;
    private ITableInfoGetter<ChannelData> writeGetter;
    private ITableInfoGetter<ChannelData> readGetter;
    private IReceiver<ChannelData> receiver;
    private volatile boolean started = false;
    
    public DBSender(DBConfig config) {
        this.config = config;
        try {
            this.daoManager = new DaoManager(getDatabaseConfig());
            // 创建数据读取器
            this.reader = Executors.newScheduledThreadPool(1, new ProxyThreadFactory("DB-Reader-"));
            // 数据发送器
            this.writer = Executors.newSingleThreadExecutor(new ProxyThreadFactory("DB-Writer-"));
            // 数据删除者
            this.deleter = Executors.newSingleThreadExecutor(new ProxyThreadFactory("DB-Deleter-"));
            // 回调者
            this.callbacker = Executors.newFixedThreadPool(2, new ProxyThreadFactory("DB-Reader-"));
            // 写表信息获取器
            this.writeGetter = tableInfoGetter(true);
            // 读表信息获取器
            this.readGetter = tableInfoGetter(false);
        } catch (Exception e) {
            log.error(StringHelper.getExceptionTrace(e));
        }
    }
    
    @Override
    public String name() {
        return "DB";
    }
    
    @Override
    public synchronized void start() throws Exception {
        if (started) {
            return;
        }
        // 获取数据库表访问对象
        IDao<ChannelData> dao = daoManager.getDao(ChannelData.class, readGetter);
        
        // 定时执行任务
        reader.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ChannelData[] datas = null;
                try {
                    datas = dao.getList(null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!ArrayHelper.isEmpty(datas)) {
                    // 删除已读
                    deleteReaded(datas, daoManager, dao);
                    // 回调接收数据接口
                    onReceiveData(datas);
                }
                
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        
        this.started = true;
    }
    
    @Override
    public synchronized void stop() throws Exception {
        if (started) {
            reader.shutdown();
            daoManager.stop();
            started = false;
        }
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
    public void setReceiver(IReceiver<ChannelData> receiver) {
        this.receiver = receiver;
    }
    
    private void deleteReaded(ChannelData[] datas, DaoManager daoManager, IDao<ChannelData> dao) {
        
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
    
    private void onReceiveData(ChannelData[] datas) {
        callbacker.execute(new Runnable() {
            @Override
            public void run() {
                if (receiver != null) {
                    receiver.onReceive(datas);
                }
            }
        });
        
    }
    
    private ITableInfoGetter<ChannelData> tableInfoGetter(boolean write) {
        TableInfo tableInfo = new PikachuTableInfoGetter<ChannelData>().getTableInfo(ChannelData.class);
        return new ITableInfoGetter<ChannelData>() {
            @Override
            public TableInfo getTableInfo(Class<ChannelData> clazz) {
                if (DBSender.this.config.isOut()) {
                    if (write) {
                        tableInfo.setTableName(DBSender.this.config.getOutWriteInTable());
                    } else {
                        tableInfo.setTableName(DBSender.this.config.getInWriteOutTable());
                    }
                } else {
                    if (write) {
                        tableInfo.setTableName(DBSender.this.config.getInWriteOutTable());
                    } else {
                        tableInfo.setTableName(DBSender.this.config.getOutWriteInTable());
                    }
                }
                return tableInfo;
            }
        };
    }
    
    private DatabaseConfig getDatabaseConfig() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setName(config.getName());
        databaseConfig.setPoolType(config.getPoolType());
        databaseConfig.setUrl(config.getUrl());
        databaseConfig.setUser(config.getUser());
        databaseConfig.setPassword(config.getPassword());
        databaseConfig.setDriver(config.getDriverClass());
        return databaseConfig;
    }
    
    @Override
    public String toString() {
        return config.toString();
    }
    
}
