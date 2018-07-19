package com.goterl.lazycode;

import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.GenericHash;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;


import static org.fusesource.jansi.Ansi.ansi;

public class Main {


    public static void main(String[] args) {
        // First setup
        setup();
        printTitle();

        if (args.length == 0) {
            printIntro();
        } else {
            // Run only if provided an argument
            String arg1 = args[0];

            try {
                Integer parsed = Integer.parseInt(arg1);
                Main main = new Main(parsed);
                main.run();
            } catch (Exception e) {
                AnsiConsole.system_err.println(
                        "Error: " + arg1 + " is not a valid number. " +
                        "Please provide a number of the operation you want to perform.");
            }
        }
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
        log("      Lazysodium for Java (Examples)       ");
        log(line);
        log();
    }

    private static void printIntro() {
        log("Please provide, as a command line argument, one of the following numbers:");
        log("1. Secret key: Perform encryption using a shared private key.");
        log("2. Public key: Encryption using public-private key.");
        log("3. Generic hashing: Hash arbitrarily.");
        log();
    }





    private Integer parsed;
    private final LazySodiumJava lazySodium;

    public Main(Integer parsed) {
        this.parsed = parsed;
        lazySodium = new LazySodiumJava(new SodiumJava());
    }


    public void run() throws SodiumException {
        if (parsed == 3) {
            genericHashStep1();
            genericHashStep2();
        }
    }


    private void genericHashStep1() throws SodiumException {
        log(" ");
        log(" >>> Running generic hash.");
        log(" ");

        // We can generate a random key or
        // we can provide a key.
        // String randomKey = lazySodium.cryptoGenericHashKeygen();

        // Key must be larger than GenericHash.KEYBYTES_MIN
        // but less than GenericHash.KEYBYTES_MAX
        String key = "AKeyThatsLongerThan16Bytes";

        log("+ Step 1: Deterministic key hash.");
        log("  The following hashes should be the same as we're using" +
                " identical keys for them.");
        log("  We will be using the key '" + key + "'.");

        String hash = lazySodium.cryptoGenericHash("", key);
        String hash2 = lazySodium.cryptoGenericHash("", key);
        log();
        log("   Hash 1: " + hash);
        log("   Hash 2: " + hash2);
        log("   Hash 1 == Hash 2? " + hash.equalsIgnoreCase(hash2));
        log();

    }

    private void genericHashStep2() throws SodiumException {
        log("+ Step 2: Random key hash.");
        log("  The following hashes should be different as we're using" +
                " random keys for them.");

        String hashRandom = lazySodium.cryptoGenericHash("", lazySodium.cryptoGenericHashKeygen());
        String hashRandom2 = lazySodium.cryptoGenericHash("", lazySodium.cryptoGenericHashKeygen());
        log();
        log("   Hash 1: " + hashRandom);
        log("   Hash 2: " + hashRandom2);
        log("   Hash 1 == Hash 2? " + hashRandom.equalsIgnoreCase(hashRandom2));
        log();

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
