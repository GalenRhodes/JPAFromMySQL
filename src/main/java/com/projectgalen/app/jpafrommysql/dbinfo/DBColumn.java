package com.projectgalen.app.jpafrommysql.dbinfo;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: DBColumn.java
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class DBColumn {

    private final DBTable        table;
    private final String         columnName;
    private final Long           ordinalPosition;
    private final String         columnDefault;
    private final boolean        isNullable;
    private final String         dataType;
    private final Long           characterMaximumLength;
    private final Long           characterOctetLength;
    private final Long           numericPrecision;
    private final Long           numericScale;
    private final Long           datetimePrecision;
    private final String         characterSetName;
    private final String         columnType;
    private final String         columnKey;
    private final String         extra;
    private final String         privileges;
    private final String         columnComment;
    private final String         generationExpression;
    private final Long           srsId;
    private final List<DBIndex>  indexes             = new ArrayList<>();
    private final List<DBColumn> toManyColumns       = new ArrayList<>();
    private       DBColumn       toOneColumn         = null;
    private       String         generatedColumnName = null;
    private       boolean        omitted             = false;

    public DBColumn(@NotNull DBTable table, @NotNull ResultSet rs) throws SQLException {
        this.table             = table;
        columnName             = rs.getString("COLUMN_NAME");
        ordinalPosition        = Utils.getLong(rs, "ORDINAL_POSITION");
        columnDefault          = rs.getString("COLUMN_DEFAULT");
        isNullable             = "YES".equals(rs.getString("IS_NULLABLE"));
        dataType               = rs.getString("DATA_TYPE");
        characterMaximumLength = Utils.getLong(rs, "CHARACTER_MAXIMUM_LENGTH");
        characterOctetLength   = Utils.getLong(rs, "CHARACTER_OCTET_LENGTH");
        numericPrecision       = Utils.getLong(rs, "NUMERIC_PRECISION");
        numericScale           = Utils.getLong(rs, "NUMERIC_SCALE");
        datetimePrecision      = Utils.getLong(rs, "DATETIME_PRECISION");
        characterSetName       = rs.getString("CHARACTER_SET_NAME");
        columnType             = rs.getString("COLUMN_TYPE");
        columnKey              = rs.getString("COLUMN_KEY");
        extra                  = rs.getString("EXTRA");
        privileges             = rs.getString("PRIVILEGES");
        columnComment          = rs.getString("COLUMN_COMMENT");
        generationExpression   = rs.getString("GENERATION_EXPRESSION");
        srsId                  = Utils.getLong(rs, "SRS_ID");
    }

    public void addIndex(@NotNull DBIndex index) {
        indexes.add(index);
        index.addColumn(this);
    }

    public void addToManyColumn(@NotNull DBColumn column) {
        toManyColumns.add(column);
    }

    public Long getCharacterMaximumLength() {
        return characterMaximumLength;
    }

    public Long getCharacterOctetLength() {
        return characterOctetLength;
    }

    public String getCharacterSetName() {
        return characterSetName;
    }

    public String getColumnComment() {
        return columnComment;
    }

    public String getColumnDefault() {
        return columnDefault;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public String getDataType() {
        return dataType;
    }

    public Long getDatetimePrecision() {
        return datetimePrecision;
    }

    public String getExtra() {
        return extra;
    }

    public String getGeneratedColumnName() {
        return generatedColumnName;
    }

    public String getGenerationExpression() {
        return generationExpression;
    }

    public List<DBIndex> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    public Long getNumericPrecision() {
        return numericPrecision;
    }

    public Long getNumericScale() {
        return numericScale;
    }

    public Long getOrdinalPosition() {
        return ordinalPosition;
    }

    public String getPrivileges() {
        return privileges;
    }

    public Long getSrsId() {
        return srsId;
    }

    public DBTable getTable() {
        return table;
    }

    public List<DBColumn> getToManyColumns() {
        return Collections.unmodifiableList(toManyColumns);
    }

    public DBColumn getToOneColumn() {
        return toOneColumn;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public boolean isOmitted() {
        return omitted;
    }

    public boolean isPrimaryKey() {
        return "PRI".equals(columnKey);
    }

    public boolean isUniquePrimaryKey() {
        return (isPrimaryKey() && (table.getPrimaryKeyColumnCount() == 1));
    }

    public void setGeneratedColumnName(String generatedColumnName) {
        this.generatedColumnName = generatedColumnName;
    }

    public void setOmitted(boolean omitted) {
        this.omitted = omitted;
    }

    public void setToOneColumn(DBColumn toOneColumn) {
        this.toOneColumn = toOneColumn;
    }

    public @Override String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(columnName).append(": ").append(dataType);
        switch(dataType) {
            case "varchar" -> sb.append("(%d)".formatted(getCharacterMaximumLength()));
            case "decimal" -> sb.append("(%d, %d)".formatted(getNumericPrecision(), getNumericScale()));
            case "datetime" -> sb.append("(%d)".formatted(getDatetimePrecision()));
        }
        sb.append(isNullable ? ", NULL" : ", NOT NULL");
        sb.append(isPrimaryKey() ? ", PRI" : "");
        Optional.ofNullable(extra).map(String::trim).filter(s -> !s.isEmpty()).ifPresent(s -> sb.append(", ").append(s));
        return sb.toString().trim();
    }
}
