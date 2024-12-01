package com.jhpark.simple_chat_socket.common.util;

import org.springframework.cloud.aws.context.support.env.AwsCloudEnvironmentCheckUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class ServerIpUtil {

    private final Environment environment;

    public ServerIpUtil(Environment environment) {
        this.environment = environment;
    }

    public String getServerIp() {
        // AWS 환경 감지
        if (AwsCloudEnvironmentCheckUtils.isRunningOnCloudEnvironment()) {
            return getAwsPrivateIp();
        } else {
            return getLocalIp();
        }
    }

    private String getAwsPrivateIp() {
        String privateIp = environment.getProperty("cloud.aws.instance.private-ip-address");
        if (privateIp != null) {
            return privateIp;
        } else {
            throw new RuntimeException("Private IP address not found in AWS metadata");
        }
    }

    private String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve local IP address", e);
        }
    }
}
