def call(Map config = [:]) {
    pipeline {
        agent any
        stages {
            stage('Checkout Code') {
                steps {
                    checkout scm
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
                        sh "docker tag ${imageName}:${imageTag} ${registry}/${imageName}:${imageTag}"
                        sh "docker push ${registry}/${imageName}:${imageTag}"
                    }
                }
            }
        }
    }
}