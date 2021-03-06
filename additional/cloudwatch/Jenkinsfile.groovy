#!groovy​

//noinspection GroovyAssignabilityCheck
pipeline {

    parameters {
        string(name: 'STACK_NAME', defaultValue: '', description: '* Cloudformation stack name id', trim: true)
        choice(name: 'REGION', choices: "us-east-1\neu-central-1", description: '* be sure that region is configured')
        choice(name: 'TEST_ROLE', defaultValue: '', description: '* IAM role to deploy Cloudformation stack', trim: true)
        choice(name: 'TEST_ACCOUNT', defaultValue: '', description: '* AWS account to provision instance', trim: true)
    }

    stages {
        stage('Clean working directory and Checkout') {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage('Set parameters') {
            steps {
                script {
                    env.TEMPLATE_PATH = "additional/cloudwatch/cloudwatch_cfn.yaml"
                }
            }
        }

        stage('Deploy CloudWatch') {
            steps {
                //noinspection GroovyAssignabilityCheck
                withAWS(role: "${TEST_ROLE}", roleAccount: "${TEST_ACCOUNT}", region: "${REGION}") {
                    cfnValidate(file: "${TEMPLATE_PATH}")
                    cfnUpdate(stack: "${STACK_NAME}", file: "${TEMPLATE_PATH}", params:[], roleArn: "${TEST_ROLE}", tags: [ "Env=${ENV}" ], pollInterval: 6000, timeoutInMinutes: 20)
                }
            }
        }
    }
}