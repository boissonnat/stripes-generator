package org.abnt.stripes.generator

import org.abnt.stripes.generator.utils.Asker
import org.abnt.stripes.generator.utils.Logger

class Runner {

    boolean verbose
    Logger log

    String projectName

    Runner(boolean verbose) {
        this.verbose = verbose
        log = new Logger()
    }

    void execute(){
        // Create project folder
        projectName = Asker.requiredAsk('Specify your project name')
        if (verbose) log.indentLog("A new project named [$projectName] will be created")

        if (!createDirectory(projectName))
            log.error("Unable to create project folder [$projectName]. This is often due to a folder already named like that")


    }

    /***********************/
    /** UTILITIES METHODS **/
    /***********************/

    private boolean createDirectory(String path){
        File dir = new File(path)
        return dir.mkdirs()
    }
}
