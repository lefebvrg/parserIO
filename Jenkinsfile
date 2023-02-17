pipeline {
    agent any
    environment {
	SONAR_HOST_URL='=http://localhost:9000'
	SONAR_LOGIN='sqp_4657f17bac0629bf48e7f49f36d43ef7e97aad9b'
    }
    stages {
	stage ('SCM') {
		steps {
			checkout scm
		}
	}
	stage ('Compile') {
		agent {	
       			docker {
       				image 'maven:3.6.0-jdk-8-alpine'
				reuseNode true
			}
		}
       		steps {
       			sh 'mvn -B -DskipTests clean package'
    		}
	}
	stage ('CheckStyle') {
		steps {
		    sh 'mvn checkstyle:checkstyle'
		}
		post {
		   always {
			recordIssues enabledForFailure: true, tool: checkStyle()
		    }
		}
	}
	stage ('Code analyze') {
		steps {
	    		sh 'mvn clean verify sonar:sonar \
				  -Dsonar.projectKey=parserIO \
				  -Dsonar.host.url=$SONAR_HOST_URL \
				  -Dsonar.login=$SONAR_LOGIN'
		}
	}
    }
}
