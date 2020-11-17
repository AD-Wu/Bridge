package com.x.bridge.proxy.bridge.impl.db;

import com.x.bridge.proxy.data.ChannelData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.util.concurrent.Callable;

public class Scanner implements Callable<ChannelData[]> {

    @Autowired
    private JdbcTemplate jdbc;

    private final String table;

    public Scanner(String table) {
        this.table = table;
    }

    @Override
    public ChannelData[] call() throws Exception {
        String sql = "select * from channel_data where status<>2 limit 64";
        //jdbc.query(sql,);
        return null;
    }

    private ChannelData map(ResultSet rs, int row){
        return null;
    }
}
