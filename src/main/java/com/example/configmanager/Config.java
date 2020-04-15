package com.example.configmanager;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Getter
@Setter
@ToString
class Config {
    private List<String> hosts = new ArrayList<>();
    private List<FileConfig> files = new ArrayList<>();
    private List<PackageConfig> packages = new ArrayList<>();
}

@Getter
@Setter
@ToString
class FileConfig {
    String src;
    String dest;
    String filename;
    String owner;
    String group;
    String mode;
    List<String> restart;
}

@Getter
@Setter
@ToString
class PackageConfig {
    String name;
    String state; // TODO: change to an Enum
}
