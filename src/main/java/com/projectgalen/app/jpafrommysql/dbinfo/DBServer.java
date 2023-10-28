package com.projectgalen.app.jpafrommysql.dbinfo;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: DBServer.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 28, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ================================================================================================================================

import com.projectgalen.app.jpafrommysql.settings.ServerInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class DBServer {

    protected final String                hostName;
    protected final int                   port;
    protected final String                username;
    protected final String                password;
    protected       Map<String, DBSchema> schemas = new TreeMap<>();

    public DBServer(@NotNull ServerInfo serverInfo) {
        this.hostName = serverInfo.getHostName();
        this.port     = serverInfo.getPortNumber();
        this.username = serverInfo.getUsername();
        this.password = serverInfo.getPassword();
    }

    public String getHostName() {
        return hostName;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public Map<String, DBSchema> getSchemas() {
        return Collections.unmodifiableMap(schemas);
    }

    public String getUsername() {
        return username;
    }

    public void loadSchema(@NotNull String schemaName) {
        DBSchema schema = new DBSchema(this, schemaName);
        schema.load();
        schemas.put(schemaName, schema);
    }

    public @Override String toString() {
        return "jdbc:mysql://%s@%s:%d".formatted(username, hostName, port);
    }
}
