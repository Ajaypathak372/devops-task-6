job('job1') {
    description('For pulling the github repo and pushing image to dokerhub')
    label('docker')
    scm {
        git {
            remote {
                url('https://github.com/Ajaypathak372/devops-task-6.git')
            }
            branch('*/' + 'master')
        }
    }
    steps{
        shell('mkdir /root/task6 ; cp -rf * /root/task6/')
        dockerBuilderPublisher {
            dockerFileDirectory('/root/task6/')
            cloud('')
            fromRegistry {
                credentialsId('')
                url('')
            }
            pushOnSuccess(false)
            cleanImages(false)
            cleanupWithJenkinsJobDelete(false)
            pushCredentialsId('cca7bb5b-f071-41ea-835e-c2e4f99bef08')
            tagsString('html-update:1.0')
        }    
    }
}

job('job2') {
    description('For setting up webservers environment and do rollouts')
    label('docker')
    scm {
        git {
            remote {
                url('https://github.com/Ajaypathak372/devops-task-6.git')
            }
            branch('*/' + 'master')
        }
    }
    triggers {
        upstream('job1', 'SUCCESS')
    }
    steps {
        shell('''mkdir /root/task6 ; cp -rf * /root/task6/ 
            if ls /root/task6 | grep *.html 
            then
            echo "Webpage type is HTML"
            name=$(kubectl get deployment --selector=type=html --output=jsonpath={.items..metadata.name})
            if [ $name =="" ]
            then
            kubectl create -f /root/task6/html.yaml
            else
            echo "HTML Webserver environment is already setup"
            fi
            else "Webpage type is not html"
            fi

            kubectl set image deployment --selector=type=html *=html-update:1.0 --record

            kubectl scale --replicas=3 deployment --selector=type=html''')
    }
}

job('job3') {
    description('For testing the webpage and sending mail if job fails')
    label('docker')
    triggers {
        upstream('job2', 'SUCCESS')
    }
    steps {
        shell('''status=$(curl -o /dev/null -s -w "%{http_code}" http://192.168.99.100:30001)
            if [ $status == 200 ]
            then 
            exit 0
            else 
            exit 1
            fi''')
    }
    publishers {
        mailer('ajaypathak372@gmail.com', false, false)
    }
}

buildPipelineView('Task6_view') {
    filterBuildQueue(false)
    filterExecutors(false)
    displayedBuilds(1)
    selectedJob('job1')
    alwaysAllowManualTrigger(true)
    showPipelineParameters()
    refreshFrequency(3)
}

if (!jenkins.model.Jenkins.instance.getItemByFullName('job1')) {
    queue('job1')
} else {
    queue('job1')
}