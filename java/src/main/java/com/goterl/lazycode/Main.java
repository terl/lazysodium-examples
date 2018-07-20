package com.goterl.lazycode;

import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.Box;
import com.goterl.lazycode.lazysodium.interfaces.GenericHash;
import com.goterl.lazycode.lazysodium.interfaces.SecretBox;
import com.goterl.lazycode.lazysodium.utils.Key;
import com.goterl.lazycode.lazysodium.utils.KeyPair;
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
        log("1. Secret key: Perform encryption using a symmetric key.");
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


    public void run() {
        try {
            if (parsed == 1) {
                symmetricKeyEncryptionStep1();
            }
            if (parsed == 2) {
                publicPrivateKeyEncryptionStep1();
            }
            if (parsed == 3) {
                genericHashStep1();
                genericHashStep1b();
                genericHashStep2();
            }
        } catch (SodiumException e) {
            e.printStackTrace();
        }
    }



    private void symmetricKeyEncryptionStep1() throws SodiumException {
        printSection("Running symmetric key encryption");

        Key key = lazySodium.cryptoSecretBoxKeygen();
        String msg = "This message needs top security";

        printStep(
                "1",
                "Encrypting a message with a symmetric key",
                "We will be using the random key '" + key.getAsHexString() + "' and will be " +
                        "encrypting the message '" + msg + "'."
        );

        byte[] nonce = lazySodium.nonce(SecretBox.NONCEBYTES);
        String encrypted = lazySodium.cryptoSecretBoxEasy(msg, nonce, key);

        log();
        logt("The encrypted string is " + encrypted + ".");
        logt("You should also store the nonce " + lazySodium.toHexStr(nonce) + ".");
        logt("The nonce can be stored in a public location.");

        symmetricKeyEncryptionStep2(key, nonce, encrypted);
    }

    private void symmetricKeyEncryptionStep2(Key key, byte[] nonce, String encrypted) throws SodiumException {

        log();
        printStep(
                "2",
                "Decrypting a message with a symmetric key",
                "We will now decrypt the message we encrypted in the previous step."
        );

        String decrypted = lazySodium.cryptoSecretBoxOpenEasy(encrypted, nonce, key);

        log();
        logt("The decrypted string is " + decrypted + ".");
        logt("It should equal the message we encrypted in step 1.");

    }





    private void publicPrivateKeyEncryptionStep1() throws SodiumException {
        printSection("Running public private key encryption");
        log();
        printStep(
                "1",
                "Generating public private keypairs",
                "In this step we'll generate public keys for Alice " +
                        "and Bob."
        );

        KeyPair aliceKp = lazySodium.cryptoBoxKeypair();
        KeyPair bobKp = lazySodium.cryptoBoxKeypair();

        log();
        logt("Alice's public key: " + aliceKp.getPublicKey().getAsHexString());
        logt("Bob's public key: " + bobKp.getPublicKey().getAsHexString());
        log();

        publicPrivateKeyEncryptionStep2(aliceKp, bobKp);
    }


    private void publicPrivateKeyEncryptionStep2(KeyPair aliceKp, KeyPair bobKp) throws SodiumException {

        String message = "Cryptography is the best";

        printStep(
                "2",
                "Encrypting a message with a public private keypair",
                "Alice wants to send the message '" + message + "' to Bob."
        );

        // Make sure that we provide the secret key of Alice encrypting
        // the message using Bob's public key.
        KeyPair aliceToBobKp = new KeyPair(bobKp.getPublicKey(), aliceKp.getSecretKey());
        byte[] nonce = lazySodium.nonce(Box.NONCEBYTES);
        String encrypted = lazySodium.cryptoBoxEasy(message, nonce, aliceToBobKp);

        log();
        logt("Alice uses her private key to encrypt the message with Bob's public key.");
        logt("Encryption result: " + encrypted);
        log();

        publicPrivateKeyEncryptionStep3(encrypted, nonce, aliceKp, bobKp);
    }

    private void publicPrivateKeyEncryptionStep3(String encrypted, byte[] nonce, KeyPair aliceKp, KeyPair bobKp) throws SodiumException {

        // Make sure we have Bob's private key decrypting the message with
        // Alice's public key.
        KeyPair bobFromAliceKp = new KeyPair(aliceKp.getPublicKey(), bobKp.getSecretKey());
        String decrypted = lazySodium.cryptoBoxOpenEasy(encrypted, nonce, bobFromAliceKp);

        logt("Bob uses his private key to decrypt the message.");
        logt("Decryption result: '" + decrypted + "'.");
        log();
    }




    private void genericHashStep1() throws SodiumException {
        printSection("Running generic hash");

        // We can generate a random key or
        // we can provide a key.
        // String randomKey = lazySodium.cryptoGenericHashKeygen();

        // Key must be larger than GenericHash.KEYBYTES_MIN
        // but less than GenericHash.KEYBYTES_MAX
        String key = "randomkeyoflength16bytes";

        printStep(
            "1",
            "Deterministic key hash",
    "The following hashes should be the same as we're using " +
                "identical keys for them.",
                "We will be using the key '" + key + "'."
        );

        String hash = lazySodium.cryptoGenericHash("", Key.fromPlainString(key));
        String hash2 = lazySodium.cryptoGenericHash("", Key.fromPlainString(key));
        log();
        logt("Hash 1: " + hash);
        logt("Hash 2: " + hash2);
        logt("Hash 1 == Hash 2? " + hash.equalsIgnoreCase(hash2));
        log();

    }

    private void printSection(String sectionTitle) {
        log(" ");
        log(" >>> " + sectionTitle + ".");
        log(" ");
    }

    private void printStep(String step, String title, String... descriptions) {
        log("+ Step " + step + ": " + title + ".");
        for (String desc : descriptions) {
            log("  " + desc);
        }
    }


    private void genericHashStep1b() throws SodiumException {
        byte[] randomKey = lazySodium.randomBytesBuf(GenericHash.KEYBYTES);

        printStep(
                "1b",
                "Deterministic key hash using Native",
                "The following hashes should be the same as we're using " +
                        "identical keys for them.",
                "We will be using the random key '" + lazySodium.toHexStr(randomKey) + "'."
        );

        GenericHash.Native nativeGH = (GenericHash.Native) lazySodium;

        byte[] message = lazySodium.bytes("Top secret message.");
        byte[] hash = lazySodium.randomBytesBuf(GenericHash.BYTES);
        byte[] hash2 = lazySodium.randomBytesBuf(GenericHash.BYTES);
        boolean res = nativeGH.cryptoGenericHash(hash, hash.length, message, message.length, randomKey, randomKey.length);
        boolean res2 = nativeGH.cryptoGenericHash(hash2, hash2.length, message, message.length, randomKey, randomKey.length);

        if (!res || !res2) {
            throw new SodiumException("Could not hash the message.");
        }

        String hash1Hex = lazySodium.toHexStr(hash);
        String hash2Hex = lazySodium.toHexStr(hash2);

        log();
        logt("Hash 1: " + hash1Hex);
        logt("Hash 2: " + hash2Hex);
        logt("Hash 1 == Hash 2? " + hash1Hex.equalsIgnoreCase(hash2Hex));
        log();

    }

    private void genericHashStep2() throws SodiumException {
        log("+ Step 2: Random key hash.");
        log("  The following hashes should be different as we're using" +
                " random keys for them.");

        String hashRandom = lazySodium.cryptoGenericHash("", lazySodium.cryptoGenericHashKeygen());
        String hashRandom2 = lazySodium.cryptoGenericHash("", lazySodium.cryptoGenericHashKeygen());
        log();
        logt("Hash 1: " + hashRandom);
        logt("Hash 2: " + hashRandom2);
        logt("Hash 1 == Hash 2? " + hashRandom.equalsIgnoreCase(hashRandom2));
        log();
    }


    // Helpers

    private static void log() {
        log("");
    }

    private static void log(String s) {
        System.out.println(s);
    }

    private static void logt(String s) {
        System.out.println("\t" + s);
    }

    private static void log(Ansi s) {
        AnsiConsole.out().println(s);
    }

}
