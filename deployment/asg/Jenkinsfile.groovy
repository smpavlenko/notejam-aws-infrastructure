#!groovy​

//noinspection GroovyAssignabilityCheck
pipeline {

    parameters {
        string(name: 'ENV', choices: "development\ntesting\nproduction", description: '* deployment environment')
        string(name: 'VPC_STACK', defaultValue: '', description: '* VPC Cloudformation stack name', trim: true)
        string(name: 'ELB_STACK', defaultValue: '', description: '* ELB Cloudformation stack name', trim: true)
        string(name: 'SQLALCHEMY_DATABASE_URI', defaultValue: '', description: '* External Database URI', trim: true)
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
                    env.TEMPLATE_PATH = "deployment/asg/asg_cfn.yaml"
                    env.PARAMS_PATH = "deployment/asg/${ENV}/${REGION}/asg_params.yaml"
                    sh "echo 'VpcStackName=${VPC_STACK}' >> ${PARAMS_PATH}"
                    sh "echo 'ElbStackName=${ELB_STACK}' >> ${PARAMS_PATH}"
                    sh "echo 'DatabaseUri=${SQLALCHEMY_DATABASE_URI}' >> ${PARAMS_PATH}"
                }
            }
        }

        stage('Deploy ASG') {
            steps {
                //noinspection GroovyAssignabilityCheck
                withAWS(role: "${TEST_ROLE}", roleAccount: "${TEST_ACCOUNT}", region: "${REGION}") {
                    cfnValidate(file: "${TEMPLATE_PATH}")
                    cfnUpdate(stack: "${STACK_NAME}", file: "${TEMPLATE_PATH}", paramsFile: "${PARAMS_PATH}", roleArn: "${TEST_ROLE}", tags: [ "Env=${ENV}" ], pollInterval: 6000, timeoutInMinutes: 20)
                }
            }
        }
    }
}