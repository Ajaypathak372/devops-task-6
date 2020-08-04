job('job1') {
    description('For pulling the github rep')
    authenticationToken('ajay123')
    scm {
        git {
            remote {
                url('https://github.com/Ajaypathak372/devops-task-6.git')
            }
            branch('*/' + 'master')
        }
    }
    steps{
        shell('mkdir /root/task6 ; cp -rf * /root/task6')
    }
}
