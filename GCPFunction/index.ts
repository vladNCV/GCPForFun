"use strict";
import {JsonConvert, JsonObject, JsonProperty, OperationMode, ValueCheckingMode} from "json2typescript";

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
