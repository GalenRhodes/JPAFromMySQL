package com.projectgalen.app.settings;

// ===========================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: ServerInfo.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 23, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerInfo {
    private @JsonProperty("hostName")   String hostName;
    private @JsonProperty("portNumber") int    portNumber;
    private @JsonProperty("schemaName") String schemaName;
    private @JsonProperty("username")   String username;
    private @JsonProperty("password")   String password;

    public ServerInfo() { }

    public ServerInfo(boolean dummy) {
        hostName   = "localhost";
        portNumber = 3306;
        schemaName = "";
        username   = "";
        password   = "";
    }

    public @Override boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ServerInfo that)) return false;
        return portNumber == that.portNumber/*@f0*/
               && Objects.equals(hostName, that.hostName)
               && Objects.equals(schemaName, that.schemaName)
               && Objects.equals(username, that.username)
               && Objects.equals(password, that.password);/*@f1*/
    }

    public String getHostName() {
        return hostName;
    }

    public String getPassword() {
        return password;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getUsername() {
        return username;
    }

    public @Override int hashCode() {
        return Objects.hash(hostName, portNumber, schemaName, username, password);
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
