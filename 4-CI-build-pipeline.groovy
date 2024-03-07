pipeline {   
  agent any
  environment {     
    DOCKERHUB_CREDENTIALS= credentials('dockerhubcredentials')     
  }    
  stages {         
    stage("Git Checkout"){           
      steps{                
        git branch: 'main', credentialsId: 'github', url: 'https://github.com/vinayprakash893/docker-voting-aws-ec2-k8s.git'
        echo 'Git Checkout Completed'            
      }        
    }
    stage('Build Docker Image') {         
      steps{                
        sh 'docker build -t vinayprakash893/vote:latest vote/'
        sh 'docker build -t vinayprakash893/result:latest result/' 
        sh 'docker build -t vinayprakash893/worker:latest worker/'            
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
        sh 'docker push vinayprakash893/vote:latest'
        sh 'docker push vinayprakash893/result:latest'
        sh 'docker push vinayprakash893/worker:latest'
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