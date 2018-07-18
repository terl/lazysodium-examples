package com.goterl.lazycode;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;

import static org.fusesource.jansi.Ansi.ansi;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class Main {


    public static void main(String args[]) {
        // First setup
        setup();
        printTitle();

        if (args.length == 0) {
            printIntro();
        }

        // Now run
        Main main = new Main();
        main.run();
    }

    private static void setup() {
        AnsiConsole.systemInstall();
    }

    private static void printTitle() {
        Ansi line = ansi()
                .fgBrightRed()
                .a(" ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
                .reset();

        log();
        log(line);
        log("        Lazysodium for Java Examples       ");
        log(line);
        log();
    }

    private static void printIntro() {
        log("Please select an option to start.");
        log();

        log("1. Secret key: Perform encryption using a shared private key.");
        log("2. Public key: Encryption using public-private key.");
        log("3. Generic hashing: Hash arbitrarily.");
    }



    public void run() {

    }




    // Helpers

    private static void log() {
        log("");
    }

    private static void log(String s) {
        System.out.println(s);
    }

    private static void log(Ansi s) {
        AnsiConsole.out().println(s);
    }

}
