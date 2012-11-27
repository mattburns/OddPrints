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
package com.oddprints.util;

public class ServerUtils {

    /**
     * Uglier url, but useful for testing unreleased versions
     * 
     * @return
     */
    public static String getAppspotHostUrl() {
        String hostUrl;
        String environment = System
                .getProperty("com.google.appengine.runtime.environment");
        if (environment != null
                && environment.toLowerCase().equals("production")) {
            String applicationId = System
                    .getProperty("com.google.appengine.application.id");
            String version = System
                    .getProperty("com.google.appengine.application.version");
            hostUrl = "http://" + version + "." + applicationId
                    + ".appspot.com";
        } else {
            hostUrl = "http://localhost:8888";
        }
        return hostUrl;
    }

    /**
     * Better for user-facing urls
     * 
     * @return
     */
    public static String getCleanHostUrl() {
        String hostUrl;
        String environment = System
                .getProperty("com.google.appengine.runtime.environment");
        if (environment != null
                && environment.toLowerCase().equals("production")) {
            hostUrl = "http://www.oddprints.com";
        } else {
            hostUrl = "http://localhost:8888";
        }
        return hostUrl;
    }
}
