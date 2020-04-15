package com.example.configmanager;

import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.pastdev.jsch.DefaultSessionFactory;

import java.util.HashMap;
import java.util.Map;


@Service
@Getter
@Setter
@ToString
public class ConfigManagerService {

    private final String username;
    private final String private_key_file;
    private static final Integer port = 22;

    public ConfigManagerService(@Value("${ssh.username:root}") String username,
                                @Value("${ssh.private_key_file}") String private_key_file) {
        this.username = username;
        this.private_key_file = private_key_file;
    }

    public DefaultSessionFactory getSessionFactory(String host) {
        DefaultSessionFactory sessionFactory = new DefaultSessionFactory(
                username, host, port
        );

        Map props = new HashMap<String, String>();
        props.put("StrictHostKeyChecking", "no");

        sessionFactory.setConfig(props);
        try {
            sessionFactory.setIdentityFromPrivateKey(private_key_file);
        } catch (JSchException exec) {
            exec.printStackTrace();
        }

        return sessionFactory;
    }

}
