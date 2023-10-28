package com.projectgalen.app.jpafrommysql;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: SQL.java
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

import com.projectgalen.lib.utils.delegates.ThrowingConsumer;
import com.projectgalen.lib.utils.delegates.ThrowingFunction;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

import static com.projectgalen.app.jpafrommysql.JPAFromMySQL.props;

@SuppressWarnings({ "SqlSourceToSinkFlow", "UnusedReturnValue", "unused" })
public final class SQL {
    private SQL() { }

    public static void doWithDatabase(String hostName, int port, String schemaName, String username, String password, @NotNull ThrowingConsumer<Connection, SQLException> consumer) {
        getFromDatabase(hostName, port, schemaName, username, password, c -> {
            consumer.accept(c);
            return null;
        });
    }

    public static void doWithPrepStmt(@NotNull Connection conn, @NotNull String sql, @NotNull ThrowingConsumer<PreparedStatement, SQLException> consumer) {
        getFromPrepStmt(conn, sql, s -> {
            consumer.accept(s);
            return null;
        });
    }

    public static void forEachRow(@NotNull PreparedStatement stmt, @NotNull ThrowingConsumer<ResultSet, SQLException> consumer) {
        try(ResultSet rs = stmt.executeQuery()) { while(rs.next()) consumer.accept(rs); } catch(SQLException e) { throw new JPASQLException(e); }
    }

    public static <R> R getFromDatabase(String hostName, int port, String schemaName, String username, String password, @NotNull ThrowingFunction<Connection, R, SQLException> func) {
        try(Connection conn = DriverManager.getConnection(props.format("jdbc.url", hostName, port, schemaName), username, password)) { return func.apply(conn); }
        catch(SQLException e) { throw new JPASQLException(e); }
    }

    public static <R> R getFromPrepStmt(@NotNull Connection conn,
                                        @NotNull String sql,
                                        @NotNull ThrowingFunction<PreparedStatement, R, SQLException> func) {
        try(PreparedStatement stmt = conn.prepareStatement(sql)) { return func.apply(stmt); } catch(SQLException e) { throw new JPASQLException(e); }
    }
}
