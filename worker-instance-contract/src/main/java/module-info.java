/**
 * @author Vlad Nicoara
 * @since 3/4/2018 TODO: Replace date with version number.
 */
module worker.instance.contract {
  requires spring.hateoas;
  requires spring.data.commons;
  requires spring.web;
  requires static lombok;
  exports com.vndemo.worker.instance.contract;
}