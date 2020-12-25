package com.ryan.project.smarthomehub.module.trait;

import com.google.cloud.firestore.DocumentSnapshot;

import java.util.Map;

/**
 * @Descritption
 * @Date 2020/12/25
 * @Author tangqianli
 */
public interface IExecution {
    String processExecution(DocumentSnapshot documentSnapshot, Map<String, Object> params);
}
