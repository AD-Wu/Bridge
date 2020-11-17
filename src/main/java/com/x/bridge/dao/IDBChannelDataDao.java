package com.x.bridge.dao;

import com.x.bridge.data.DBChannelData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDBChannelDataDao extends JpaRepository<DBChannelData,Long> {

}
