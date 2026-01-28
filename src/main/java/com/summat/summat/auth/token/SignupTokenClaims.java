package com.summat.summat.auth.token;

public final class SignupTokenClaims {
    private SignupTokenClaims() {}

    public static final String PURPOSE = "purpose";
    public static final String EMAIL = "email";
    public static final String TYPE = "type";

    public static final String TYPE_SIGNUP = "email-verification";
}

