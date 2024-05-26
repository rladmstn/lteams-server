package com.lck.server.exception;

import java.util.ArrayList;

public record ErrorResponse(int status,
							String error,
							ArrayList<String> messages) {
}
