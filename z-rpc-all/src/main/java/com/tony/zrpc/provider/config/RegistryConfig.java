package com.tony.zrpc.provider.config;

/**
 * @author herrhu
 * @date 2021/12/17 19:45
 **/
public class RegistryConfig {
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "RegistryConfig{" +
                "address='" + address + '\'' +
                '}';
    }
}
