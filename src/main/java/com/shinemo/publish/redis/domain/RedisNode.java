package com.shinemo.publish.redis.domain;

import com.google.common.base.Strings;

/**
 * redis 相关的配置信息
 */
public class RedisNode {

    private String ip;
    private int port;
    private String password;
    private int db;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }

    public RedisNode(String ip, int port, String password) {
        this.ip = ip;
        this.port = port;
        this.password = password;
    }

    public RedisNode(String ip, int port, String password, int db) {
        this(ip, port, password);
        this.db = db;
    }

    public RedisNode(String redisConfig) {
        if (!Strings.isNullOrEmpty(redisConfig)) {
            String[] temp = redisConfig.split(":");
            if (temp.length < 3) {
                throw new IllegalArgumentException("redis param error");
            }
            this.ip = temp[0];
            this.port = Integer.parseInt(temp[1]);
            this.password = temp[2];
        } else {
            throw new IllegalArgumentException("redis param is null");
        }
    }

    @Override
    public String toString() {
        return "RedisNode [ip=" + ip + ", port=" + port + ", password=" + password + ", db=" + db + "]";
    }

    @Override
    public int hashCode() {
        return (ip + "," + port + "," + password + "," + db).hashCode();
    }

}
