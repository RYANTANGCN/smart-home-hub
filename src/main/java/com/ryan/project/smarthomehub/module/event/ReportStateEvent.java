package com.ryan.project.smarthomehub.module.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Getter
public class ReportStateEvent extends ApplicationEvent {

    private String agentUserId;

    private Map<String, Object> states;

    public ReportStateEvent(Object source, String agentUserId, Map states) {
        super(source);
        this.agentUserId = agentUserId;
        this.states = states;
    }
}
