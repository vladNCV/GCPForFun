/*
package com.example.worker.frontend;

import com.example.workerinstanceinterface.EmployeeCrudRest;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;

*/
package com.example.worker.frontend;

import com.vndemo.worker.instance.contract.EmployeeCrudRest;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Vlad Nicoara
 * @since 2/28/2018 TODO: Replace date with version number.
 */

@FeignClient("worker-instance-backend")
interface EmployeeCrudRestClient extends EmployeeCrudRest {

}
