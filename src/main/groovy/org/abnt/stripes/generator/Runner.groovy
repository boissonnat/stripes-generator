package org.abnt.stripes.generator

import org.abnt.stripes.generator.utils.Asker
import org.abnt.stripes.generator.utils.Logger
import groovy.text.GStringTemplateEngine

class Runner {

    boolean verbose
    Logger log

    String artifactId
    String groupId
    String packageName
    String versionNumber
    boolean useGroovy

    Runner(boolean verbose) {
        this.verbose = verbose
        log = new Logger()
    }

    void execute(){
        // Create project folder
        artifactId = Asker.requiredAsk('Specify your project name')
        groupId = Asker.requiredAsk('Specify your maven groupId').toLowerCase()
        versionNumber = Asker.askWithDefault('Specify your version number', '1.0-SNAPSHOT')
        packageName = Asker.askWithDefault('Specify your package name', groupId+"."+artifactId).toLowerCase()
        useGroovy = Asker.yesNoAsk('Would you like to use Groovy language')

        if (verbose) log.indentLog("A new project named [$artifactId] will be created")

        if (!createDirectory(artifactId))
            log.error("Unable to create project folder [$artifactId]. This is often due to a folder already named like that")

        // Launch generation steps
        createPom()
        createPackages()

    }

    private void createPom(){

        def binding = [:]
        binding['artifactId'] = artifactId
        binding['groupId'] = groupId
        binding['versionNumber'] = versionNumber
        binding['useGroovy'] = useGroovy

        FileWriter writer = new FileWriter(artifactId+File.separator+'pom.xml')
        generateTemplate(binding, 'pom-xml', false, writer)

        // Summary
        log.indentLog('- Maven pom.xml file created')
    }

    private void createPackages(){
        String srcBasePath, testBasePath
        if (useGroovy){
            srcBasePath = artifactId + File.separator + 'src'+File.separator+'main'+File.separator+'groovy'
            testBasePath = artifactId + File.separator + 'src'+File.separator+'test'+File.separator+'groovy'
        }else{
            srcBasePath = artifactId + File.separator + 'src'+File.separator+'main'+File.separator+'java'
            testBasePath = artifactId + File.separator + 'src'+File.separator+'test'+File.separator+'java'
        }

        String srcPath = srcBasePath + File.separator+packageName.replaceAll("\\.", "\\"+File.separator)
        String testPath = testBasePath + File.separator+packageName.replaceAll("\\.", "\\"+File.separator)
        String actionBeanPath = srcPath+File.separator+'actions'

        String srcResources = artifactId + File.separator + 'src'+File.separator+'main'+File.separator+'resources'
        String testResources = artifactId + File.separator + 'src'+File.separator+'test'+File.separator+'resources'
        String webApp = artifactId + File.separator + 'src'+File.separator+'main'+File.separator+'webapp'+
                File.separator+'WEB-INF'

        if (!createDirectory(actionBeanPath)){
            log.error('An error occurs during the actionBean source directory creation')
        }
        if (!createDirectory(testPath)){
            log.error('An error occurs during the maven TEST directory creation')
        }
        if (!createDirectory(srcResources)){
            log.error('An error occurs during the maven SRC RESOURCES directory creation')
        }
        if (!createDirectory(testResources)){
            log.error('An error occurs during the maven TEST RESOURCES directory creation')
        }
        if (!createDirectory(webApp)){
            log.error('An error occurs during the webapp directory creation')
        }
    }

    /***********************/
    /** UTILITIES METHODS **/
    /***********************/

    private boolean createDirectory(String path){
        File dir = new File(path)
        return dir.mkdirs()
    }

    private void generateTemplate(Map binding, String templateName, boolean useGroovy, Writer out){
        def engine = new GStringTemplateEngine()
        def tpl = templateName + (useGroovy ? "-groovy.template" : ".template")
        engine.createTemplate(this.class.getResource(tpl)).make(binding).writeTo(out)
        out.flush()
        out.close()
    }
}