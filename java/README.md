# Lazysodium for Java Examples

This project shows some example code for the Java variant of Lazysodium.

## Running

#### Gradle

To run the code via Gradle please use the following command:

```
$ gradle run
```

Which will output a list of options, for example:

```
1. Secret key.
2. Public private key encryption.
3. Generic hashing.
4. Generic hashing.
5. Signing
```

Then use an option number from the above list to run the code that you want. For example:
```
$ gradle run --args "3"
```
