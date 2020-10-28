package com.ryan.project.smarthomehub.module.auth.service.impl;

import com.ryan.project.smarthomehub.module.auth.dao.ClientStoreDao;
import com.ryan.project.smarthomehub.module.auth.domain.entity.ClientStore;
import com.ryan.project.smarthomehub.module.auth.service.IClientStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientStoreServiceImpl implements IClientStoreService {

    @Autowired
    ClientStoreDao clientStoreDao;

    @Override
    public boolean validateClientId(String clientId) {
        if (clientStoreDao.countByClientId(clientId) == 1) {
            return true;
        }
        return false;
    }

    /*@Override
    public boolean validateClientIdAndClientSecret(String clientId, String clientSecret) {
        if (clientStoreDao.getClientStoreByClientIdAndClientSecret(clientId, clientSecret) != null) {
            return true;
        }
        return false;
    }*/

    @Override
    public ClientStore getClientStoreByClientIdAndClientSecret(String clientId, String clientSecret) {
        return clientStoreDao.getClientStoreByClientIdAndClientSecret(clientId, clientSecret);
    }

}
