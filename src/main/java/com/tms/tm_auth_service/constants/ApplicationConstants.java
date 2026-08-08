package com.tms.tm_auth_service.constants;

public final class ApplicationConstants {

	private ApplicationConstants() {
	}

	public static final String JWT_SECRET_KEY = "JWT_SECRET";
	public static final String JWT_EXPIRATION_KEY = "JWT_EXPIRATION_MS";
	public static final String JWT_SECRET_DEFAULT_VALUE = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
	public static final long JWT_EXPIRATION_DEFAULT_MS = 30_000_000L;
	public static final String JWT_HEADER = "Authorization";
	public static final String JWT_ISSUER = "TMS Auth Service";
}
