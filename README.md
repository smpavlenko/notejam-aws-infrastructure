# Notejam AWS infrastructure

This repository contains an example of infrastructure for on-premise application which is going to be deployed in AWS public cloud.
The application itself can be found in the git repo [notejam](https://github.com/nordcloud/notejam)

## Business Requirements
- The Application must serve variable amount of traffic. Most users are active during business hours. During big events and conferences the traffic could be 4 times more than typical.
- The Customer takes guarantee to preserve your notes up to 3 years and recover it if needed.
- The Customer ensures continuity in service in case of data center failures.
- The Service must be capable of being migrated to any regions supported by the cloud provider in case of emergency.
- The Customer is planning to have more than 100 developers to work in this project who want to roll out multiple deployments a day without interruption / downtime.
- The Customer wants to provision separated environments to support their development process for development, testing, production in the near future.
- The Customer wants to see relevant metrics and logs from the infrastructure for quality assurance and security purposes.

## Assumptions
- Let’s assume we have 10M total users, with 1M daily active users.
- Let's assume 10:1 ratio between read and write.
- Let's assume 10M daily read requests with 1M daily write requests.
- Let's assume 10:1 ratio between Notes and Pads.
- Let's assume 10 Pads and 50 Notes per user.

#### Traffic estimates
- 10M / (24 hours * 3600 seconds) ~= 120 reads/sec
- 1M / (24 hours * 3600 seconds) ~= 12 writes/sec

#### DataBase Storage estimates
- Let’s assume we have three database tables User, Pad, Note with fields
  - User table: ID 2B, Email 120B, Password 100B
  - Pad table: ID 2B, Name 100B
  - Note table: ID 2B, Name 100B, Text 1KB
- User table storage (2B + 120B + 100B) * 10M ~= 2GB total
- Pad table storage (2B + 100B) * 10 * 10M ~= 1GB total
- Note table storage (2B + 120B + 1KB) * 50 * 10M ~= 540GB total
- Total Data Base storage needed (2GB + 1GB + 540GB) ~= 544GB total

#### Bandwidth estimates
- (2B + 120B + 1KB) * 120 reads ~= 140 KB/s for read requests
- (2B + 120B + 1KB) * 12 writes ~= 14 KB/s for write requests

## Minimum viable product
There are three possible environment configurations - development, testing, production.
- All of the configurations include VPC, three public subnets, internet gateway 
- Development environment includes security group with opened port 5000 and single instance 
- Testing and production environments include Classic Load Balancer, two security groups, Autoscalig group and Scaling Policies based on CPU Cloudwatch Alarms
- Production environment must have provided deployed MySql DataBase 
