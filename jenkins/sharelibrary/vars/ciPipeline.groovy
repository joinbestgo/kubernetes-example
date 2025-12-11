def call(Map config = [:]) {
    pipeline {
        agent any
        stages {
            stage('Checkout Code') {
                steps {
                    checkout scm
                    sh 'echo Code Checked Out.'
                    sh 'whoami' 
                }
            }
            stage('Run Unit Tests') {
                steps {
                    sh 'echo Running Unit Tests...'
                    sh './mvn test' // 示例：运行 Gradle 测试
                }
            }
            stage('Build Docker Image') {
                steps {
                    script {
                        def imageName = config.imageName ?: 'default-image'
                        def imageTag = config.imageTag ?: 'latest'
                        sh "docker build -t ${imageName}:${imageTag} ."
                    }
                }
            }
            stage('Push Docker Image') {
                steps {
                    script {
                        def imageName = config.imageName ?: 'default-image'
                        def imageTag = config.imageTag ?: 'latest'
                        def registry = config.registry ?: 'docker.io'

                        // 使用 Jenkins 凭据
                        withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                            sh "echo ${DOCKER_PASSWORD} | docker login ${registry} --username ${DOCKER_USERNAME} --password-stdin"
                            sh "docker tag ${imageName}:${imageTag} ${registry}/${imageName}:${imageTag}"
                            sh "docker push ${registry}/${imageName}:${imageTag}"
                        }
                    }
                }
            }
        }
    }
}