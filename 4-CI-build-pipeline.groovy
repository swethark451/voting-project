pipeline {   
  agent any
  environment {     
    DOCKERHUB_CREDENTIALS= credentials('dockerhubcredentials')     
  }    
  stages {         
    stage("Git Checkout"){           
      steps{                
        git branch: 'main', credentialsId: 'Github', url: 'https://github.com/swethark451/voting-project'
        echo 'Git Checkout Completed'            
      }        
    }
    stage('Build Docker Image') {         
      steps{                
        sh 'docker build -t swethark451/vote:latest vote/'
        sh 'docker build -t swethark451/result:latest result/' 
        sh 'docker build -t swethark451/worker:latest worker/'            
        echo 'Build Image Completed'                
      }           
    }
    stage('Login to Docker Hub') {         
      steps{                            
        sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'                 
        echo 'Login Completed'                
      }           
    }               
    stage('Push Image to Docker Hub') {         
      steps{                            
        sh 'docker push swethark451/vote:latest'
        sh 'docker push swethark451/result:latest'
        sh 'docker push swethark451/worker:latest'
        echo 'Push Image Completed'       
      }           
    }      
  } 
  post{
    always {  
      sh 'docker logout'           
    }      
  }  
} 
