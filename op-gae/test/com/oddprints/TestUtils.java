package com.oddprints;

import java.util.Properties;

import com.oddprints.dao.ApplicationSetting;

public enum TestUtils {

    INSTANCE;

    public void addSettingsFromTestFile() {
        Properties prop = new Properties();

        try {
            // load a properties file
            prop.load(getClass()
                    .getResourceAsStream("test-settings.properties"));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't find test-settings.properties", e);
        }

        for (Object key : prop.keySet()) {
            String keyString = (String) key;
            ApplicationSetting.putSetting(keyString,
                    prop.getProperty(keyString));
        }
    }
}
