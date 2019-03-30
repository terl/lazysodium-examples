package com.goterl.lazycode;

import com.goterl.lazycode.lazysodium.LazySodium;
import com.goterl.lazycode.lazysodium.LazySodiumJava;
import com.goterl.lazycode.lazysodium.SodiumJava;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.*;
import com.goterl.lazycode.lazysodium.utils.Key;
import com.goterl.lazycode.lazysodium.utils.KeyPair;
import com.sun.jna.NativeLong;
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
            } catch (NumberFormatException e) {
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
        log("4. Password hashing: Password hash.");
        log("5. Sign (detached): Sign a message in detached mode.");
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
            if (parsed == 4) {
                pwHashStep1();
            }
            if (parsed == 5) {
                sign();
                signWithNonRandomKeys();
            }
        } catch (SodiumException e) {
            e.printStackTrace();
        }
    }

    private void sign() throws SodiumException {
        printSection("Running sign detached");
        String msg = "This message needs to be signed";


        KeyPair kp = lazySodium.cryptoSignKeypair();
        Key pk = kp.getPublicKey();
        Key sk = kp.getSecretKey();

        if (!lazySodium.cryptoSignKeypair(pk.getAsBytes(), sk.getAsBytes())) {
            throw new SodiumException("Could not generate a signing keypair.");
        }

        printStep(
                "1",
                "Signing a message",
                "We will be using the random secret key '" + sk.getAsHexString() + "' and will be " +
                        "signing the message '" + msg + "'."
        );

        byte[] messageBytes = lazySodium.bytes(msg);
        byte[] skBytes = sk.getAsBytes();
        byte[] signatureBytes = new byte[Sign.BYTES];

        lazySodium.cryptoSignDetached(signatureBytes, null, messageBytes, messageBytes.length, skBytes);
        boolean v = lazySodium.cryptoSignVerifyDetached(signatureBytes, messageBytes, messageBytes.length, pk.getAsBytes());

        log();
        logt("The signed message is " + LazySodium.toHex(signatureBytes) + ".");
        logt("Verifying the signed message outputs true: " + v + ".");

    }

    private void signWithNonRandomKeys() throws SodiumException {
        String msg = "Sign this";
        Key pk = Key.fromPlainString("edpkuBknW28nW72KG6RoHtYW7p12T6GKc7nAbwYX5m8Wd9sDVC9yav8888888888");
        Key sk = Key.fromPlainString("edsk3gUfUPyBSfrS9CCgmCiQsTCHGkviBDusMxDJstFtojtc1zcpsh8888888888");

        log();

        printStep(
                "2",
                "Signing a message (non-random)",
                "We will be using the non-random secret key '" + sk.getAsHexString() + "' and will be " +
                        "signing the message '" + msg + "'."
        );



        if (!lazySodium.cryptoSignKeypair(pk.getAsBytes(), sk.getAsBytes())) {
            throw new SodiumException("Could not generate a signing keypair.");
        }

        String signed = lazySodium.cryptoSignDetached(msg, sk);
        boolean verification = lazySodium.cryptoSignVerifyDetached(signed, msg, pk);

        log();
        logt("The signed message is " + signed + ".");
        logt("Verifying the signed message outputs true: " + verification + ".");

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


    private void pwHashStep1() {
        printSection("Running password hashing");
        String pw = "superAwesomePassword";

        printStep(
                "1",
                "Hashing a password",
                "Attempting to hash a password '" + pw + "' using Argon 2."
        );

        try {
            String hash = lazySodium.cryptoPwHashStr(pw, 2L, new NativeLong(65536));
            log();
            logt("Password hashing successful: " + hash);
            log();
        } catch (SodiumException e) {
            e.printStackTrace();
            log();
            logt("Password hashing failed with exception: " + e.getMessage());
            log();
        }

        pwHashStep2();
    }

    private void pwHashStep2() {
        String pw = "lol";

        printStep(
                "2",
                "Multiple password verification (Native)",
                "Verifying password '" + pw + "' using Argon2 many times. " +
                        "This may take a while..."
        );

        byte[] pwBytes = lazySodium.bytes(pw);

        // Remember the terminating byte (null byte) at the end of the hash!
        // As this is using the Native interface you must always remember
        // to add that null byte yourself. Use the Lazy interface if you
        // don't want to handle any of that (as shown in the next step).
        byte[] hash = lazySodium.bytes(
                "$argon2id$v=19$m=65536,t=2,p=1$ZrWMVZiMs4tvs0QwVc7T7A$L" +
                "Il6XlgIZsuozRpC3bCe5ew8LEWgDQvQE8qwsZ9ISps\0"
        );

        log();
        int i = 0;
        while (i < 100) {
            boolean result = lazySodium.cryptoPwHashStrVerify(hash, pwBytes, pwBytes.length);
            logt("Password hashing verification: " + result);
            i++;
        }
        log();

        pwHashStep3();

    }


    private void pwHashStep3() {
        String pw = "password";

        printStep(
                "2",
                "Multiple password hashing (Lazy)",
                "Hashing password '" + pw + "' using Argon2 lazy methods. " +
                        "This also may take a while..."
        );

        log();
        int i = 0;

        // In the following while loop, we keep hashing the above password
        // then we verify it. If at any point we aren't successful we log it.
        while (i < 30) {
            try {
                // You can also remove the null bytes at the end of this hex hash
                // using cryptoPwHashStrRemoveNulls instead of
                // cryptoPwHashStr, but that is not recommended
                // as Argon2 needs at least one null byte
                // at the end.
                String hash = lazySodium.cryptoPwHashStr(pw, 2, PwHash.MEMLIMIT_MIN);

                // To get an Argon2 hash instead of a hex hash,
                // lazySodium.str(lazySodium.toBinary(hash)) is one way to do that.

                logt("Password hashing successful: " + hash);
                boolean result = lazySodium.cryptoPwHashStrVerify(hash, pw);
                logt("Password hashing verification: " + result);
            } catch (SodiumException e) {
                logt("Password hashing unsuccessful: " + e.getMessage());
            }
            i++;
        }
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
