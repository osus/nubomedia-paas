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

package org.project.openbaton.nubomedia.paas.model.persistence.security;

import org.openbaton.catalogue.nfvo.Quota;
import org.openbaton.catalogue.util.IdGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lto on 24/05/16.
 */
@Entity
public class Project {
  @Id private String id;

  @Column(unique = true)
  private String name;

  private String description;

  @Column(columnDefinition = "LONGBLOB")
  private HashMap<String, Role.RoleEnum> users;

  private Quota quota;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Quota getQuota() {
    return quota;
  }

  public void setQuota(Quota quota) {
    this.quota = quota;
  }

  @PrePersist
  public void ensureId() {
    id = IdGenerator.createUUID();
  }

  public Map<String, Role.RoleEnum> getUsers() {
    return users;
  }

  public void setUsers(HashMap<String, Role.RoleEnum> users) {
    this.users = users;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "Project{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", users="
        + users
        + ", quota="
        + quota
        + '}';
  }
}
