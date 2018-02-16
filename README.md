# GoogleCloudCEDemo

Test project to be build and containerized automatically by google cloud container registry.

GCPFunction can be deployed using "serverless deploy" but it requires a GCP IAM key file with the appropriate permissions in ".gcloud/keyfile.json".

GCPFunction uses:
- Node.js
- Typescript
- TsLint
- Serverless framework for deployment

Using:
  - Java 9 (openjdk)
  - Project Lombok
  - Junit 5
  - Spring :
    - Boot :
        - Core
        - WebFlux (reactive support)
        - Actuator
        - DevTools
        - Configuration Processor
    - Cloud Config Server
  - Google Cloud :
    - Compute Engine
    - Function
    - Load Balancer & Auto-Scaling
    - Container Builder
