package org.abnt.stripes.generator

class StripesGenerator {

    public static void main(String[] args){
        // Display Welcome message
        println """   _____ _        _                 _____
  / ____| |      (_)               / ____|
 | (___ | |_ _ __ _ _ __   ___ ___| |  __  ___ _ __
  \\___ \\| __| '__| | '_ \\ / _ | __| | |_ |/ _ \\ '_ \\
  ____) | |_| |  | | |_) |  __|__ \\ |__| |  __/ | | |
 |_____/ \\__|_|  |_| .__/ \\___|___/\\_____|\\___|_| |_|
                   | |
                   |_|            Version 1.0 powered by Alex
----------------------------------------------------------------------------
"""

        // Check Arguments
        if (!args) {
            // No arguments -> Error
            println 'stripesGen: missing argument'
            println 'Try `stripes --help` for more information.'

        }else {
            boolean verbose = false
            for (int i=0 ; i<args.length ; i++) {
                String a = args[i]

                if (a == '-h' || a == '--help'){
                    // Display Help
                    println 'Usage: stripesGen [OPTION]... init'
                    println '\t --help :\t\tDisplay help'
                    println '\t -h :\t\tDisplay help'
                    println '\t -v :\t\tSwitch into verbose mode'
                    break
                } else {
                    if (a == '-v'){
                        // Run in verbose mode
                        verbose = true
                    }else{
                        if(a == 'init'){
                            Runner runner = new Runner(verbose)
                            runner.execute()
                        }else{
                            // Wrong arguments -> Error
                            println 'stripesGen: unknown argument'
                            println 'Try `stripes --help` for more information.'
                        }
                    }
                }
            }
        }
    }
}
