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

/**
 * @author Vlad Nicoara
 * @since 2/22/2018
 */

"use strict";
import {
  JsonConvert,
  JsonObject,
  JsonProperty,
  OperationMode,
  ValueCheckingMode
} from "json2typescript";

@JsonObject
class Employee {
  @JsonProperty("id", Number)
  public id: number = 0;
  @JsonProperty("name", String)
  public name: string = "";
}

function extractEmployee(body: string): Employee {
  if (body == null) {
    throw new Error("Event body is null. Cannot extract Employee.");
  }
  const jsonConvert: JsonConvert = new JsonConvert();
  jsonConvert.operationMode = OperationMode.ENABLE; // change to LOGGING to print data
  jsonConvert.ignorePrimitiveChecks = false; // don't allow assigning number to string etc.
  jsonConvert.valueCheckingMode = ValueCheckingMode.DISALLOW_NULL; // never allow null
  return jsonConvert.deserialize(body, Employee);
}

const helloWorld = (req: any, res: any) => {
  console.log(req);
  console.log(req.body);
  try {
    const employee: Employee = extractEmployee(req.body);
    res.status(200).send("Hello from lambda " + employee.name + " !");
  } catch (e) {
    console.log(e as Error);
    res.status(400).send((e as Error).message);
  }
};

export {helloWorld};
