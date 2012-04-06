package org.abnt.stripes.generator.utils

class Logger {

    void error(String message){
        println('AN ERROR OCCURS :')
        indentLog(message)
        println('The program will exit')
        System.exit(1)
    }

    void indentLog(String message){
        println """| $message"""
    }
}
