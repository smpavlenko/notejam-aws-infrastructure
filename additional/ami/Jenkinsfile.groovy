#!groovy​

//noinspection GroovyAssignabilityCheck
pipeline {

    parameters {
        string(name: 'VPC_ID', defaultValue: '', description: '* VPC id', trim: true)
        choice(name: 'SUBNET_ID', defaultValue: '', description: '* Subnet id', trim: true)
        choice(name: 'SECURITY_GROUP_ID', defaultValue: '', description: '* Security Group id', trim: true)
        choice(name: 'REGION', choices: "us-east-1\neu-central-1", description: '* be sure that region is configured')
        choice(name: 'TEST_ROLE', defaultValue: '', description: '* IAM role to provision instance', trim: true)
        choice(name: 'TEST_ACCOUNT', defaultValue: '', description: '* AWS account to provision instance', trim: true)
    }

    stages {
        stage('Clean working directory and Checkout') {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage('Build AMI') {
            steps {
                //noinspection GroovyAssignabilityCheck
                withAWS(role: "${TEST_ROLE}", roleAccount: "${TEST_ACCOUNT}", region: "${REGION}") {
                    dir('additional/ami/packer') {
                        sh "packer build -color=false -var 'vpc=${VPC_ID}' -var 'subnet_id=${SUBNET_ID}' -var 'security_group=${SECURITY_GROUP_ID}' ami.json"

                        script {
                            env.AMI_ID = sh returnStdout: true, script: "cat packer-manifest.json | jq -r '.builds[] | select (.name==\"ami\") | .artifact_id'"
                            if (!env.AMI_ID?.trim()) {
                                error("AMI ID empty, build failed")
                            }
                            env.AMI_ID = AMI_ID.contains(':') ? AMI_ID.tokenize(':')[1].trim() : AMI_ID.trim()
                            echo "Created AMI: ${AMI_ID}"
                        }
                    }
                }
            }
        }

        stage('Report') {
            steps {
                script {
                    currentBuild.description = "${AMI_ID}"
                }
            }
        }
    }
}