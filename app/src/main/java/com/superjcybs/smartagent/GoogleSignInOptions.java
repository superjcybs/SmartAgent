package com.superjcybs.smartagent;

public class GoogleSignInOptions {
    public static final Object DEFAULT_SIGN_IN = null;

    public static class Builder {
        public Builder(Object p0) {
        }
        // You'd typically have a build() method in a Builder pattern
        public GoogleSignInOptions build() {
            // Logic to construct and return a GoogleSignInOptions instance
            return new GoogleSignInOptions(); // Or however you construct it
        }

//        public Builder requestEmail() {
//        }
    }

    // You might need a private constructor if using a Builder
//    private GoogleSignInOptions() {
//    }
}
