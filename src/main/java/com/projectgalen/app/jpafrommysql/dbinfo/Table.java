package com.projectgalen.app.jpafrommysql.dbinfo;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: Table.java
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

import com.projectgalen.app.jpafrommysql.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@SuppressWarnings("unused")
public class Table {

    private final String              tableCatalog;
    private final String              tableSchema;
    private final String              tableName;
    private final String              tableType;
    private final String              engine;
    private final Long                version;
    private final String              rowFormat;
    private final Long                tableRows;
    private final Long                avgRowLength;
    private final Long                dataLength;
    private final Long                maxDataLength;
    private final Long                indexLength;
    private final Long                dataFree;
    private final Long                autoIncrement;
    private final Timestamp           createTime;
    private final Timestamp           updateTime;
    private final Timestamp           checkTime;
    private final String              tableCollation;
    private final String              checksum;
    private final String              createOptions;
    private final String              tableComment;
    private final Map<String, Column> columns                = new LinkedHashMap<>();
    private final Map<String, Index>  indexes                = new TreeMap<>();
    private       List<Column>        primaryKeyColumns      = null;
    private       int                 primaryKeyColumnCount  = -1;
    private final List<ForeignKey>    referencedForeignKeys  = new ArrayList<>();
    private final List<ForeignKey>    referencingForeignKeys = new ArrayList<>();

    public Table(@NotNull ResultSet rs) throws SQLException {
        tableCatalog   = rs.getString("TABLE_CATALOG");
        tableSchema    = rs.getString("TABLE_SCHEMA");
        tableName      = rs.getString("TABLE_NAME");
        tableType      = rs.getString("TABLE_TYPE");
        engine         = rs.getString("ENGINE");
        version        = Utils.getLong(rs, "VERSION");
        rowFormat      = rs.getString("ROW_FORMAT");
        tableRows      = Utils.getLong(rs, "TABLE_ROWS");
        avgRowLength   = Utils.getLong(rs, "AVG_ROW_LENGTH");
        dataLength     = Utils.getLong(rs, "DATA_LENGTH");
        maxDataLength  = Utils.getLong(rs, "MAX_DATA_LENGTH");
        indexLength    = Utils.getLong(rs, "INDEX_LENGTH");
        dataFree       = Utils.getLong(rs, "DATA_FREE");
        autoIncrement  = Utils.getLong(rs, "AUTO_INCREMENT");
        createTime     = rs.getTimestamp("CREATE_TIME");
        updateTime     = rs.getTimestamp("UPDATE_TIME");
        checkTime      = rs.getTimestamp("CHECK_TIME");
        tableCollation = rs.getString("TABLE_COLLATION");
        checksum       = rs.getString("CHECKSUM");
        createOptions  = rs.getString("CREATE_OPTIONS");
        tableComment   = rs.getString("TABLE_COMMENT");
    }

    public void addColumns(@NotNull ResultSet rs) throws SQLException {
        while(rs.next()) columns.put(rs.getString("COLUMN_NAME"), new Column(this, rs));
    }

    public void addIndexes(@NotNull ResultSet rs) throws SQLException {
        while(rs.next()) {
            Column column = columns.get(rs.getString("Column_name"));
            Index  index  = indexes.computeIfAbsent(rs.getString("Key_name"), k -> getIndex(rs));
            if((column != null) && (index != null)) column.addIndex(index);
        }
    }

    public void addReferencedForeignKey(@NotNull ForeignKey key) {
        referencedForeignKeys.add(key);
    }

    public void addReferencingForeignKey(@NotNull ForeignKey key) {
        referencingForeignKeys.add(key);
    }

    public Long getAutoIncrement() {
        return autoIncrement;
    }

    public Long getAvgRowLength() {
        return avgRowLength;
    }

    public Timestamp getCheckTime() {
        return checkTime;
    }

    public String getChecksum() {
        return checksum;
    }

    public Column getColumn(@NotNull String name) {
        return columns.get(name);
    }

    public List<Column> getColumns() {
        return columns.values().stream().toList();
    }

    public String getCreateOptions() {
        return createOptions;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Long getDataFree() {
        return dataFree;
    }

    public Long getDataLength() {
        return dataLength;
    }

    public String getEngine() {
        return engine;
    }

    public Long getIndexLength() {
        return indexLength;
    }

    public List<Index> getIndexes() {
        return indexes.values().stream().toList();
    }

    public Long getMaxDataLength() {
        return maxDataLength;
    }

    public int getPrimaryKeyColumnCount() {
        return ((primaryKeyColumnCount < 0) ? ((primaryKeyColumnCount = getPrimaryKeyColumns().size())) : primaryKeyColumnCount);
    }

    public List<Column> getPrimaryKeyColumns() {
        return ((primaryKeyColumns == null) ? (primaryKeyColumns = columns.values().stream().filter(Column::isPrimaryKey).toList()) : primaryKeyColumns);
    }

    public List<ForeignKey> getReferencedForeignKeys() {
        return Collections.unmodifiableList(referencedForeignKeys);
    }

    public List<ForeignKey> getReferencingForeignKeys() {
        return Collections.unmodifiableList(referencingForeignKeys);
    }

    public String getRowFormat() {
        return rowFormat;
    }

    public String getTableCatalog() {
        return tableCatalog;
    }

    public String getTableCollation() {
        return tableCollation;
    }

    public String getTableComment() {
        return tableComment;
    }

    public String getTableName() {
        return tableName;
    }

    public Long getTableRows() {
        return tableRows;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public String getTableType() {
        return tableType;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public Long getVersion() {
        return version;
    }

    public boolean isInnoBD() {
        return "InnoDB".equals(engine);
    }

    public @Override String toString() {
        return tableName;
    }

    private @Nullable Index getIndex(@NotNull ResultSet rs) {
        try { return new Index(this, rs); } catch(SQLException ignore) { return null; }
    }
}
