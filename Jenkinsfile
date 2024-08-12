pipeline{
  agent any
  stages{
    stage('Build'){
      steps{
        sh 'C:\apache-maven-3.9.8-bin\apache-maven-3.9.8\bin\mvn clean install'
      }
    }
      stage('Test'){
      steps{
        sh 'C:\apache-maven-3.9.8-bin\apache-maven-3.9.8\bin\mvn test'
      }
    }
  }
}
