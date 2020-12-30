package com.contracyclix.chuckchess.config;

import lombok.Setter;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

public class Config {
    @Setter
    private static String configFileName = "application.properties";

    private static Config singleton = null;

    private PropertiesConfiguration config;

    private Config() {
        Configurations configs = new Configurations();
        File propertiesFile = new File(configFileName);

        try {
            config = configs.properties(propertiesFile);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesConfiguration get() {
        if (singleton == null)
            singleton = new Config();
        return singleton.config;
    }
}
