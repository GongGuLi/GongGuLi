package back.request.account;

import java.io.Serializable;

public record Find_UserId_Request(String name, String birth, String phoneNumber) implements Serializable {}