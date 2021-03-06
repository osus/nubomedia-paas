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

package org.project.openbaton.nubomedia.paas.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * Created by maa on 22.01.16.
 */
@Service
@ConfigurationProperties(prefix = "kms")
public class KmsProperties {

  private String image;

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }
}
