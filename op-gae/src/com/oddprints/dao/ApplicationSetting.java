/*******************************************************************************
 * Copyright 2011 Matt Burns
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.oddprints.dao;

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.common.collect.Maps;
import com.oddprints.PMF;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ApplicationSetting {

    public enum Settings {
        APPLICATION_ENVIRONMENT, PWINTY_MERCHANT_ID, PWINTY_MERCHANT_KEY, GOOGLE_CHECKOUT_MERCHANT_KEY_SANDBOX, GOOGLE_CHECKOUT_MERCHANT_ID_SANDBOX, GOOGLE_CHECKOUT_MERCHANT_KEY_LIVE, GOOGLE_CHECKOUT_MERCHANT_ID_LIVE, SAMPLE_PHOTO_BLOB_KEY, SAMPLE_PHOTO_BLOB_SIZE;
    }

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key id;

    @Persistent
    private String key;

    @Persistent
    private String value;

    public ApplicationSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static void putSetting(Settings key, String value) {
        putSetting(key.toString(), value);
    }

    public static void putSetting(String keyString, String value) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        List<ApplicationSetting> settings = getSettingObjects(pm);
        for (ApplicationSetting setting : settings) {
            if (setting.getKey().equals(keyString)) {
                setting.setValue(value);
                pm.makePersistent(setting);
                pm.close();
                return;
            }
        }
        ApplicationSetting setting = new ApplicationSetting(keyString, value);
        pm.makePersistent(setting);
        pm.close();
    }

    public static Map<String, String> getSettings() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        List<ApplicationSetting> applicationSettings = getSettingObjects(pm);

        Map<String, String> map = Maps.newTreeMap();
        for (ApplicationSetting setting : applicationSettings) {
            map.put(setting.getKey(), setting.getValue());
        }
        return map;
    }

    public static String getSetting(Settings setting) {
        return getSetting(setting.toString());
    }

    public static String getSetting(String key) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        List<ApplicationSetting> applicationSettings = getSettingObjects(pm);

        Map<String, String> map = Maps.newTreeMap();
        for (ApplicationSetting setting : applicationSettings) {
            map.put(setting.getKey(), setting.getValue());
        }
        return map.get(key);
    }

    private static List<ApplicationSetting> getSettingObjects(
            PersistenceManager pm) {
        Query query = pm.newQuery(ApplicationSetting.class);

        query.setOrdering("key asc");
        @SuppressWarnings("unchecked")
        List<ApplicationSetting> applicationSettings = (List<ApplicationSetting>) query
                .execute();
        return applicationSettings;
    }

    public static void deleteSetting(String keyString) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        List<ApplicationSetting> settings = getSettingObjects(pm);
        for (ApplicationSetting setting : settings) {
            if (setting.getKey().equals(keyString)) {
                pm.deletePersistent(setting);
                pm.close();
                return;
            }
        }
    }
}
