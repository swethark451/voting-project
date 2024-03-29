pipeline {   
  agent any
  environment {     
    DOCKERHUB_CREDENTIALS= credentials('dockerhubcredentials')
    AWS_ACCESS_KEY_ID     = credentials('AWS_ACCESS_KEY_ID')
    AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')


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
        sh 'docker build -t swethark451/vote:latest 3-code-3-app/vote/'
        sh 'docker build -t swethark451/result:latest 3-code-3-app/result/' 
        sh 'docker build -t swethark451/worker:latest 3-code-3-app/worker/'            
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
    stage('login'){
      steps{
      withCredentials([string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'), string(credentialsId: 'AWS_SECRET_ACCESS_KEY', variable: 'AWS_SECRET_ACCESS_KEY')])
      {     
        sh """
            aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
            aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
        """
        sh 'aws s3 ls'
      }
    }}
    
    stage("kubernetes deployment"){
      steps{
        sh 'aws eks --region us-west-2 update-kubeconfig --name clusterVote'
        sh 'kubectl version'
        sh 'kubectl apply -f 4-voting-app-loadbalancerIP'
        echo 'Completed'       
      }    } 
  } 
  post{
    always {  
      sh 'docker logout'           
    }      
  }  
} 
