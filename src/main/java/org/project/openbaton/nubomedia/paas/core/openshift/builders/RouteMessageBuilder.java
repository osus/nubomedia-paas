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

package org.project.openbaton.nubomedia.paas.core.openshift.builders;

import org.project.openbaton.nubomedia.paas.model.openshift.*;

/**
 * Created by maa on 25/09/2015.
 */
public class RouteMessageBuilder {

  private String osName;
  private String domainName;
  private RouteTls tls;

  public RouteMessageBuilder(String osName, String domainName, RouteTls tls) {
    this.osName = osName;
    this.domainName = domainName;
    this.tls = tls;
  }

  public RouteConfig buildMessage() {

    Metadata metadata = new Metadata(osName + "-route", "", "");
    BuildElements be = new BuildElements("Service", osName + "-svc");
    RouteSpec spec = new RouteSpec(osName + "." + domainName, be, tls, new RouteSpec.Status());

    return new RouteConfig(metadata, spec);
  }
}
