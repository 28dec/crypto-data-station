pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "ghcr.io/28dec/crypto-data-station:${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/28dec/crypto-data-station.git'
            }
        }

        stage('Build') {
            steps {
                // Build the project using Gradle
                sh './gradlew clean build -x test'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image using the generated JAR file
                    docker.build(DOCKER_IMAGE, "-f Dockerfile .")
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://ghcr.io', 'github-container-registry') {
                        docker.image(DOCKER_IMAGE).push()
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                    sh '''
                    docker pull ghcr.io/28dec/crypto-data-station:${BUILD_NUMBER} || true &&
                    docker stop crypto-data-station || true &&
                    docker rm crypto-data-station || true &&
                    docker run -d --name crypto-data-station --env-file ~/crypto-data-station/env.txt -p 7980:8080 ghcr.io/28dec/crypto-data-station:${BUILD_NUMBER}
                    '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
