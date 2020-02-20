# NoteJam AWS infrastructure

This repository contains an example of infrastructure for on-premise application which is going to be deployed in AWS public cloud.
The application itself can be found in the git repo https://github.com/nordcloud/notejam

## Business Requirements
- The Application must serve variable amount of traffic. Most users are active during business hours. During big events and conferences the traffic could be 4 times more than typical.
- The Customer takes guarantee to preserve your notes up to 3 years and recover it if needed.
- The Customer ensures continuity in service in case of data center failures.
- The Service must be capable of being migrated to any regions supported by the cloud provider in case of emergency.
- The Customer is planning to have more than 100 developers to work in this project who want to roll out multiple deployments a day without interruption / downtime.
- The Customer wants to provision separated environments to support their development process for development, testing, production in the near future.
- The Customer wants to see relevant metrics and logs from the infrastructure for quality assurance and security purposes.