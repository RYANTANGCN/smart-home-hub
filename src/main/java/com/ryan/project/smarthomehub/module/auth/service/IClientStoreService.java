package com.ryan.project.smarthomehub.module.auth.service;

import com.ryan.project.smarthomehub.module.auth.domain.entity.ClientStore;

public interface IClientStoreService {

    boolean validateClientId(String clientId);

    ClientStore getClientStoreByClientId(String clientId);
}
