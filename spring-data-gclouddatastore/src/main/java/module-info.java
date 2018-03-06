/**
 * @author Vlad Nicoara
 * @since 2/22/2018 TODO: Replace date with version number.
 */
// Netflix ribbon uses split packages, these do not work with java 9 modules.
// Need to either disable module or remove dependency until fix is released.
module spring.data.gclouddatastore {
  requires google.cloud.datastore;
  requires spring.data.commons;
  requires spring.context;
  requires spring.beans;
  requires spring.core;
  requires java.desktop;
  requires google.cloud.core;
  requires java.sql;
  requires protobuf.java;
  requires slf4j.api;
  requires proto.google.common.protos;
  requires spring.boot.autoconfigure;
  requires spring.jcl;
  requires static lombok;
  exports org.springframework.data.gclouddatastore.repository;
}
