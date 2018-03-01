/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.gclouddatastore.repository;

import org.springframework.data.annotation.Id;

public class Person {

  @Id
  private long id;

  private String emailAddress;

  private String firstName;

  private String lastName;

  private int birthYear;

  private boolean citizen;

  Person(long id) {
    this.id = id;
  }

  public Person() {
  }

  Person(
      long id,
      String emailAddress,
      String firstName,
      String lastName,
      int birthYear,
      boolean citizen) {
    this.id = id;
    this.emailAddress = emailAddress;
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthYear = birthYear;
    this.citizen = citizen;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public int getBirthYear() {
    return birthYear;
  }

  public void setBirthYear(int birthYear) {
    this.birthYear = birthYear;
  }

  public boolean isCitizen() {
    return citizen;
  }

  public void setCitizen(boolean citizen) {
    this.citizen = citizen;
  }
}
