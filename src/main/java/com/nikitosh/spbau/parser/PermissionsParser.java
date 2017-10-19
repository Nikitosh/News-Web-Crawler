package com.nikitosh.spbau.parser;

public interface PermissionsParser {
    Permissions getPermissions(String url);
    double getDelay(String url);
}
