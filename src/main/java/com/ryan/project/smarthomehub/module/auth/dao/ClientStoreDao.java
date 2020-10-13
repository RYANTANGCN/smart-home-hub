package com.ryan.project.smarthomehub.module.auth.dao;

import com.ryan.project.smarthomehub.module.auth.domain.entity.ClientStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientStoreDao extends JpaRepository<ClientStore, Integer> {

    int countByClientId(String clientId);
}
