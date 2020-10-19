package com.ryan.project.smarthomehub.module.auth.dao;

import com.ryan.project.smarthomehub.module.auth.domain.entity.ClientStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientStoreDao extends JpaRepository<ClientStore, Integer> {

    int countByClientId(String clientId);

    ClientStore getClientStoreByClientIdAndClientSecret(String clientId, String clientSecret);

    ClientStore getClientStoreByClientId(String clientId);
}
