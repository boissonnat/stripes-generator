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
    String srcBasePath
    String testBasePath
    String jspFolder
    String commonJspFolder
    String actionBeanPath

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

        if (!createDirectory(artifactId))
            log.error("Unable to create project folder [$artifactId]. This is often due to a folder already named like that")

        // Launch generation steps
        createPom()
        createPackages()
        createResources()
        createWebXml()
        createJsp()
        createActionBean()

        println '\n'
        log.indentLog('A new Stripes project has been created : ')
        log.indentLog("\t cd $artifactId")
        log.indentLog("\t Run Jetty with : mvn jetty:run")
        log.indentLog("\t Point your browser at : http://localhost:8080/$artifactId")

    }

    private void createPom(){

        def binding = [:]
        binding['artifactId'] = artifactId
        binding['groupId'] = groupId
        binding['versionNumber'] = versionNumber

        FileWriter writer = new FileWriter(artifactId+File.separator+'pom.xml')
        generateTemplate(binding, 'pom-xml', useGroovy, writer)

        // Summary
        println '\n'
        log.indentLog('Maven integration pom file created :')
        log.indentLog('\t- All dependencies needed by Stripes v1.5.6 have been added')
        log.indentLog(useGroovy ? '\t- Using Groovy language' : '\t- Using pure Java')
        log.indentLog('\t- Jetty maven plugin configured (try jetty:run)')

    }

    private void createPackages(){

        if (useGroovy){
            srcBasePath = artifactId + File.separator + 'src'+File.separator+'main'+File.separator+'groovy'
            testBasePath = artifactId + File.separator + 'src'+File.separator+'test'+File.separator+'groovy'
        }else{
            srcBasePath = artifactId + File.separator + 'src'+File.separator+'main'+File.separator+'java'
            testBasePath = artifactId + File.separator + 'src'+File.separator+'test'+File.separator+'java'
        }

        String srcPath = srcBasePath + File.separator+packageName.replaceAll("\\.", "\\"+File.separator)
        String testPath = testBasePath + File.separator+packageName.replaceAll("\\.", "\\"+File.separator)
        actionBeanPath = srcPath+File.separator+'actions'

        String srcResources = artifactId + File.separator + 'src'+File.separator+'main'+File.separator+'resources'
        String testResources = artifactId + File.separator + 'src'+File.separator+'test'+File.separator+'resources'
        String webApp = artifactId + File.separator + 'src'+File.separator+'main'+File.separator+'webapp'+
                File.separator+'WEB-INF'

        jspFolder = webApp + File.separator + 'jsp'
        commonJspFolder = jspFolder + File.separator + 'common'

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

        if (!createDirectory(jspFolder)){
            log.error('An error occurs during the jsp directory creation')
        }
        if (!createDirectory(commonJspFolder)){
            log.error('An error occurs during the commonJspFolder directory creation')
        }

        // Summary
        println '\n'
        log.indentLog('Some folders have been created')
        String packageSrcStr = useGroovy ? '\t- src/main/groovy/' : '\t- src/main/java/'
        log.indentLog(packageSrcStr + packageName.replaceAll("\\.", "\\/") + '/actions : Your actionBean package')
        log.indentLog('\t- src/main/resources : Your applications resources')
        log.indentLog('\t- src/main/webapp : Your web resources')
        String packageTestStr = useGroovy ? '\t- src/test/groovy/' : '\t- src/test/java/'
        log.indentLog(packageTestStr + packageName.replaceAll("\\.", "\\/") + ' : Your test package')
        log.indentLog('\t- src/test/resources : Your test resources')
    }

    private void createResources(){
        // Generate default resources first
        def binding = [:]
        binding['artifactId'] = artifactId

        FileWriter writerDefault = new FileWriter(artifactId+File.separator+'src/main/resources/StripesResources.properties')
        generateTemplate(binding, 'StripesResources', false, writerDefault)

        boolean french = Asker.yesNoAsk("Would you like to use French language")
        if (french){
            FileWriter writerFrench = new FileWriter(artifactId+File.separator+'src/main/resources/StripesResources_fr.properties')
            generateTemplate(binding, 'StripesResources_fr', false, writerFrench)
        }
        // Summary
        println '\n'
        log.indentLog('Localization files generated :')
        log.indentLog('\t- Default language is english : /src/main/resources/StripesResources.properties')
        if(french)
            log.indentLog('\t- French language is available : /src/main/resources/StripesResources_fr.properties')

    }

    private void createWebXml(){
        def binding = [:]
        binding['packageActions'] = packageName+'.actions'
        binding['packageStripesExt'] = packageName+'.stripes.ext'
        binding['packageSecurityManager'] = packageName+'.stripes.noext.MySecurityManager'
        binding['packageMyInitListener'] = packageName+'.stripes.noext.MyInitListener'

        FileWriter writerDefault = new FileWriter(artifactId+File.separator+'src/main/webapp/WEB-INF/web.xml')
        generateTemplate(binding, 'web-xml', false, writerDefault)
        // Summary
        println '\n'
        log.indentLog('web.xml file generated :')
        log.indentLog('\t- /src/main/webapp/WEB-INF/web.xml')
    }

    private void createJsp(){
        copyFile(this.class.getResourceAsStream('/index.jsp'), artifactId+File.separator+'src/main/webapp/index.jsp')
        copyFile(this.class.getResourceAsStream('/home.jsp'), jspFolder + File.separator + 'home.jsp')
        copyFile(this.class.getResourceAsStream('/layout.jsp'), commonJspFolder + File.separator + 'layout.jsp')
        copyFile(this.class.getResourceAsStream('/taglibs.jsp'), commonJspFolder + File.separator + 'taglibs.jsp')

        // Summary
        println '\n'
        log.indentLog('Jsp\'s files have been generated :')
        log.indentLog('\t- /src/main/webapp/index.jsp')
        log.indentLog('\t- /src/main/webapp/jsp/home.jsp')
        log.indentLog('\t- /src/main/webapp/jsp/common/layout.jsp')
        log.indentLog('\t- /src/main/webapp/jsp/common/taglibs.jsp')
    }

    private void createActionBean(){
        def binding = [:]
        binding["packageActions"] = packageName+'.actions'
        FileWriter writerBase
        FileWriter writerHome
        if (useGroovy) {
            writerBase = new FileWriter(actionBeanPath+File.separator+'BaseActionBean.groovy')
            writerHome = new FileWriter(actionBeanPath+File.separator+'HomeActionBean.groovy')
        }else{
            writerBase = new FileWriter(actionBeanPath+File.separator+'BaseActionBean.java')
            writerHome = new FileWriter(actionBeanPath+File.separator+'HomeActionBean.java')
        }

        generateTemplate(binding, 'base-actionbean', useGroovy, writerBase)
        generateTemplate(binding, 'home-actionbean', useGroovy, writerHome)


        // Summary
        println '\n'
        log.indentLog('ActionBeans have been generated:')
        log.indentLog('\t- BaseActionBean')
        log.indentLog('\t- HomeActionBean')
    }

    /***********************/
    /** UTILITIES METHODS **/
    /***********************/

    private boolean createDirectory(String path){
        File dir = new File(path)
        return dir.mkdirs()
    }

    private void copyFile(InputStream srcAsInputStream, String dest){
        new File(dest) << srcAsInputStream
    }

    private void generateTemplate(Map binding, String templateName, boolean useGroovy, Writer out){
        def engine = new GStringTemplateEngine()
        def tpl = templateName + (useGroovy ? "-groovy.template" : ".template")
        engine.createTemplate(this.class.getResource(tpl)).make(binding).writeTo(out)
        out.flush()
        out.close()
    }
}
