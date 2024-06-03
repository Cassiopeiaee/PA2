pipeline {
  agent any

  environment {
    GIT_REPO_URL = 'https://github.com/Cassiopeiaee/Nutzerverwaltung'
    GIT_BRANCH = 'master'
    GIT_CREDENTIALS = 'Git'
  }

  stages {
    stage ('Checkout') {
      steps {
      git branch: "${env.GIT_BRANCH}", url:"${env.GIT_REPO_URL}",  credentialsId: "${env.GIT_CREDENTIALS}"
    }
  }

  
    stage ('Build') {
      steps {
        echo 'Building...'
        bat 'mvn install'
      }
    }
    

    stage ('Test') {
      steps {
        echo 'Testing...'
        bat 'mvn test'
      }
    }


    stage ('Deploy') {
      steps {
        echo 'Deploying...'
        bat 'mvn deploy'
      }
    }
  }
    post {
      always {
        echo 'Cleaning...'
        sh 'mvn clean'
      }
      success {
        echo 'Pipeline success'
      }
      failure {
        echo 'Pipeline failed'
      }
    }
  }
