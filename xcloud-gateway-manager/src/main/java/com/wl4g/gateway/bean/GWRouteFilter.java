package com.wl4g.gateway.bean;

import com.wl4g.components.core.bean.BaseBean;

public class GWRouteFilter extends BaseBean {
    private static final long serialVersionUID = -3298424126317938674L;

    private Integer routeId;

    private String name;

    private String args;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args == null ? null : args.trim();
    }
}