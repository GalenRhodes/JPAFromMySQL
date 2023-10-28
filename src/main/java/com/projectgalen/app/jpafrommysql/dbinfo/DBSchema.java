package com.projectgalen.app.jpafrommysql.dbinfo;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: DBSchema.java
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

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static com.projectgalen.app.jpafrommysql.JPAFromMySQL.sql;
import static com.projectgalen.app.jpafrommysql.SQL.*;
import static com.projectgalen.app.jpafrommysql.Utils.setStmtString;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
public class DBSchema {

    protected final String               schemaName;
    protected final DBServer             server;
    protected final Map<String, DBTable> tables = new TreeMap<>();

    public DBSchema(@NotNull DBServer server, @NotNull String schemaName) {
        this.schemaName = schemaName;
        this.server = server;
    }

    public String getSchemaName()                    { return schemaName; }

    public @NotNull Map<String, DBTable> getTables() { return Collections.unmodifiableMap(tables); }

    public @Override String toString()               { return "%s - [%,d tables]".formatted(schemaName, tables.size()); }

    protected void load() {
        doWithDatabase(server.getHostName(), server.getPort(), getSchemaName(), server.getUsername(), server.getPassword(), conn -> {
            tables.clear();
            doWithPrepStmt(conn, sql.getProperty("fetch.tables"), stmt -> forEachRow(setStmtString(stmt, 1, getSchemaName()), this::loadTable));
            doWithPrepStmt(conn, sql.getProperty("fetch.columns"), stmt -> tables.values().forEach(table -> table.load(setStmtString(stmt, 1, schemaName))));
            tables.forEach((name, table) -> doWithPrepStmt(conn, sql.format("fetch.indexes", getSchemaName(), name), table::addIndexes));
            doWithPrepStmt(conn, sql.getProperty("fetch.foreign_keys"), stmt -> forEachRow(setStmtString(stmt, 1, getSchemaName()), this::loadForeignKey));
        });
    }

    private @NotNull DBForeignKey loadForeignKey(ResultSet rs) throws SQLException { return new DBForeignKey(tables, rs); }

    private void loadTable(@NotNull ResultSet rs) throws SQLException              { tables.put(rs.getString("TABLE_NAME"), new DBTable(this, rs)); }
}
