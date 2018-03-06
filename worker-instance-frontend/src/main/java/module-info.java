/**
 * @author Vlad Nicoara
 * @since 0.0.1
 */
module worker.instance.frontend {
  requires spring.boot.autoconfigure;
  requires spring.boot;
  requires spring.web;
  requires spring.beans;
  requires worker.instance.contract;
  requires reactor.core;
  requires spring.cloud.openfeign.core;
  requires org.apache.logging.log4j;
  requires static lombok;
  requires spring.context;
}