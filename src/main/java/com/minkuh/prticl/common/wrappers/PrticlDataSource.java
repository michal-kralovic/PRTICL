package com.minkuh.prticl.common.wrappers;

public record PrticlDataSource(String serverName, int port, String database, String user, String password, String schema) {
    public String url() {
        return "jdbc:postgresql://" + serverName + ':' + port + '/' + database;
    }
}