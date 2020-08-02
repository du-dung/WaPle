package com.ssafy.waple.error.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
	// Common
	INVALID_INPUT_VALUE(400, "C001", "Invalid Input Value"),
	DUPLICATED_KEY(400, "C002", "Duplicated Key"),
	ENTITY_NOT_FOUND(400, "C003", "Entity Not Found"),
	INVALID_TYPE_VALUE(400, "C005", "Invalid Type Value"),
	HANDLE_ACCESS_DENIED(403, "C006", "Access is Denied"),
	NOT_FOUND(404, "C004", "Not Found"),
	METHOD_NOT_ALLOWED(405, "C002", "Invalid Input Value"),

	// Group
	GROUP_IS_NOT_EMPTY(409, "G001", "Group is not empty"),

	// User
	OWNER_NOT_ALLOW(412, "U001", "Group owner can not leave"),

	;

	private final String code;
	private final String message;
	private final int status;

	ErrorCode(final int status, final String code, final String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return this.message;
	}

	public String getCode() {
		return code;
	}

	public int getStatus() {
		return status;
	}
}
