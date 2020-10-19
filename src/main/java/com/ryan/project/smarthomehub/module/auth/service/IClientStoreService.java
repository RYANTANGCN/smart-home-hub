package com.ryan.project.smarthomehub.module.auth.service;

import com.ryan.project.smarthomehub.module.auth.domain.entity.ClientStore;

public interface IClientStoreService {

    boolean validateClientId(String clientId);

//    boolean validateClientIdAndClientSecret(String clientId, String clientSecret);

    ClientStore getClientStoreByClientIdAndClientSecret(String clientId, String clientSecret);

}
