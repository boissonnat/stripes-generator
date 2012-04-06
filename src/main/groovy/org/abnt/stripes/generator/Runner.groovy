package org.abnt.stripes.generator

import org.abnt.stripes.generator.utils.Asker

class Runner {

    boolean verbose

    String projectName

    Runner(boolean verbose) {
        this.verbose = verbose
    }

    void execute(){
        // Create project folder
        projectName = Asker.requiredAsk('Specify your project name')
        if (verbose) println "A new project named [$projectName] will be created"
    }
}
