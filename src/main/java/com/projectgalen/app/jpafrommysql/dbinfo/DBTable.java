package com.projectgalen.app.jpafrommysql.dbinfo;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: DBTable.java
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

import com.projectgalen.app.jpafrommysql.JPASQLException;
import com.projectgalen.app.jpafrommysql.Utils;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static com.projectgalen.app.jpafrommysql.SQL.forEachRow;
import static com.projectgalen.app.jpafrommysql.Utils.setStmtString;

@SuppressWarnings("unused")
public class DBTable {

    protected final DBSchema              schema;
    protected final String                tableCatalog;
    protected final String                tableName;
    protected final String                tableType;
    protected final String                engine;
    protected final Long                  version;
    protected final String                rowFormat;
    protected final Long                  tableRows;
    protected final Long                  avgRowLength;
    protected final Long                  dataLength;
    protected final Long                  maxDataLength;
    protected final Long                  indexLength;
    protected final Long                  dataFree;
    protected final Long                  autoIncrement;
    protected final Timestamp             createTime;
    protected final Timestamp             updateTime;
    protected final Timestamp             checkTime;
    protected final String                tableCollation;
    protected final String                checksum;
    protected final String                createOptions;
    protected final String                tableComment;
    protected final Map<String, DBColumn> columns                = new LinkedHashMap<>();
    protected final Map<String, DBIndex>  indexes                = new TreeMap<>();
    protected final List<DBColumn>        primaryKeyColumns      = new ArrayList<>();
    protected final List<DBForeignKey>    referencedForeignKeys  = new ArrayList<>();
    protected final List<DBForeignKey>    referencingForeignKeys = new ArrayList<>();

    protected String  generatedTableName = null;
    protected boolean omitted            = false;

    public DBTable(@NotNull DBSchema schema, @NotNull ResultSet rs) throws SQLException {
        this.schema = schema;
        tableCatalog   = rs.getString("TABLE_CATALOG");
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

    public void addReferencedForeignKey(@NotNull DBForeignKey key) {
        referencedForeignKeys.add(key);
    }

    public void addReferencingForeignKey(@NotNull DBForeignKey key) {
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

    public DBColumn getColumn(@NotNull String name) {
        return columns.get(name);
    }

    public List<DBColumn> getColumns() {
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

    public String getGeneratedTableName() {
        return generatedTableName;
    }

    public Long getIndexLength() {
        return indexLength;
    }

    public List<DBIndex> getIndexes() {
        return indexes.values().stream().toList();
    }

    public Long getMaxDataLength() {
        return maxDataLength;
    }

    public int getPrimaryKeyColumnCount() {
        return primaryKeyColumns.size();
    }

    public List<DBColumn> getPrimaryKeyColumns() {
        return Collections.unmodifiableList(primaryKeyColumns);
    }

    public List<DBForeignKey> getReferencedForeignKeys() {
        return Collections.unmodifiableList(referencedForeignKeys);
    }

    public List<DBForeignKey> getReferencingForeignKeys() {
        return Collections.unmodifiableList(referencingForeignKeys);
    }

    public String getRowFormat() {
        return rowFormat;
    }

    public DBSchema getSchema() {
        return schema;
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
        return schema.schemaName;
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

    public boolean isOmitted() {
        return omitted;
    }

    public void setGeneratedTableName(String generatedTableName) {
        this.generatedTableName = generatedTableName;
    }

    public void setOmitted(boolean omitted) {
        this.omitted = omitted;
    }

    public @Override String toString() {
        return tableName;
    }

    protected void addIndexes(@NotNull PreparedStatement stmt) {
        forEachRow(stmt, rs -> {
            DBColumn column = columns.get(rs.getString("Column_name"));
            DBIndex  index  = indexes.computeIfAbsent(rs.getString("Key_name"), k -> { try { return new DBIndex(this, rs); } catch(SQLException e) { throw new JPASQLException(e); } });
            if(column != null) column.addIndex(index);
        });
    }

    protected void load(@NotNull PreparedStatement stmt) {
        forEachRow(setStmtString(stmt, 2, tableName), rs -> {
            DBColumn column = new DBColumn(this, rs);
            columns.put(column.getColumnName(), column);
            if(column.isPrimaryKey()) primaryKeyColumns.add(column);
        });
    }
}
