package com.projectgalen.app.jpafrommysql.settings;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: ServerInfo.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 25, 2023
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projectgalen.app.jpafrommysql.JPAFromMySQL;
import com.projectgalen.lib.utils.delegates.ThrowingConsumer;
import com.projectgalen.lib.utils.delegates.ThrowingFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.sql.*;
import java.util.Objects;

import static com.projectgalen.app.jpafrommysql.JPAFromMySQL.props;

@SuppressWarnings("SqlSourceToSinkFlow")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerInfo {
    public static final int DEFAULT_MYSQL_PORT = 3306;

    private @JsonProperty("hostName")   String hostName;
    private @JsonProperty("portNumber") int    portNumber;
    private @JsonProperty("schemaName") String schemaName;
    private @JsonProperty("username")   String username;
    private @JsonProperty("password")   String password;

    public ServerInfo() { }

    public ServerInfo(boolean dummy) {
        hostName   = "localhost";
        portNumber = DEFAULT_MYSQL_PORT;
        schemaName = "";
        username   = "";
        password   = "";
    }

    public void doWithDatabase(@NotNull ThrowingConsumer<Connection, SQLException> consumer) {
        doWithDatabase(false, consumer);
    }

    public void doWithDatabase(boolean suppressErrorDialog, @NotNull ThrowingConsumer<Connection, SQLException> consumer) {
        getFromDatabase(suppressErrorDialog, c -> {
            consumer.accept(c);
            return null;
        });
    }

    public void doWithPrepStmt(@NotNull Connection conn, @NotNull String sql, @NotNull ThrowingConsumer<PreparedStatement, SQLException> consumer) throws SQLException {
        doWithPrepStmt(conn, sql, false, consumer);
    }

    public void doWithPrepStmt(@NotNull Connection conn, @NotNull String sql, boolean suppressErrorDialog, @NotNull ThrowingConsumer<PreparedStatement, SQLException> consumer) throws SQLException {
        getFromPrepStmt(conn, sql, s -> {
            consumer.accept(s);
            return null;
        });
    }

    public void doWithPrepStmt(@NotNull String sql, @NotNull ThrowingConsumer<PreparedStatement, SQLException> consumer) {
        doWithPrepStmt(sql, false, consumer);
    }

    public void doWithPrepStmt(@NotNull String sql, boolean suppressErrorDialog, @NotNull ThrowingConsumer<PreparedStatement, SQLException> consumer) {
        doWithDatabase(conn -> doWithPrepStmt(conn, sql, suppressErrorDialog, consumer));
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

    public void forEachRow(@NotNull PreparedStatement stmt, @NotNull ThrowingConsumer<ResultSet, SQLException> consumer) throws SQLException {
        try(ResultSet rs = stmt.executeQuery()) { while(rs.next()) consumer.accept(rs); }
    }

    public <R> @Nullable R getFromDatabase(@NotNull ThrowingFunction<Connection, R, SQLException> func) {
        return getFromDatabase(false, func);
    }

    public <R> @Nullable R getFromDatabase(boolean suppressErrorDialog, @NotNull ThrowingFunction<Connection, R, SQLException> func) {
        try(Connection conn = DriverManager.getConnection(props.format("jdbc.url", getHostName(), getPortNumber(), getSchemaName()), getUsername(), getPassword())) {
            return func.apply(conn);
        }
        catch(SQLException e) {
            if(suppressErrorDialog) e.printStackTrace(System.err);
            else JOptionPane.showMessageDialog(JPAFromMySQL.app(), e, "Database Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public <R> @Nullable R getFromPrepStmt(@NotNull Connection conn, @NotNull String sql, @NotNull ThrowingFunction<PreparedStatement, R, SQLException> func) throws SQLException {
        return getFromPrepStmt(conn, sql, false, func);
    }

    public <R> @Nullable R getFromPrepStmt(@NotNull Connection conn,
                                           @NotNull String sql,
                                           boolean suppressErrorDialog,
                                           @NotNull ThrowingFunction<PreparedStatement, R, SQLException> func) throws SQLException {
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            return func.apply(stmt);
        }
        catch(SQLException e) {
            if(suppressErrorDialog) e.printStackTrace(System.err);
            else JOptionPane.showMessageDialog(JPAFromMySQL.app(), e, "Database Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public <R> @Nullable R getFromPrepStmt(@NotNull String sql, @NotNull ThrowingFunction<PreparedStatement, R, SQLException> func) {
        return getFromPrepStmt(sql, false, func);
    }

    public <R> @Nullable R getFromPrepStmt(@NotNull String sql, boolean suppressErrorDialog, @NotNull ThrowingFunction<PreparedStatement, R, SQLException> func) {
        return getFromDatabase(conn -> getFromPrepStmt(conn, sql, suppressErrorDialog, func));
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
