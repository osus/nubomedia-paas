/*
 *
 *  * Copyright (c) 2016 Open Baton
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.project.openbaton.nubomedia.paas.core.openshift;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by maa on 20.10.15.
 */
public class AvoidRedirectRequestFactory extends SimpleClientHttpRequestFactory {

  @Override
  protected void prepareConnection(HttpURLConnection connection, String httpMethod)
      throws IOException {
    connection.setDoInput(true);

    connection.setInstanceFollowRedirects(false);

    if ("PUT".equals(httpMethod)
        || "POST".equals(httpMethod)
        || "PATCH".equals(httpMethod)
        || "DELETE".equals(httpMethod)) {
      connection.setDoOutput(true);
    } else {
      connection.setDoOutput(false);
    }
    connection.setRequestMethod(httpMethod);
  }
}
