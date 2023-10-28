package com.projectgalen.app.jpafrommysql.dbinfo;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: DBForeignKey.java
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

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static java.util.Optional.ofNullable;

@SuppressWarnings("unused")
public class DBForeignKey {

    private final String         constraintName;
    private final String         tableName;
    private final String         columnName;
    private final String         referencedTableName;
    private final String         referencedColumnName;
    private final long           ordinalPosition;
    private final long           positionInUniqueConstraint;
    private final DBColumn       column;
    private final DBColumn       referencedColumn;
    private final ForeignKeyType type;

    public DBForeignKey(@NotNull Map<String, DBTable> tables, @NotNull ResultSet rs) throws SQLException {
        constraintName             = rs.getString("CONSTRAINT_NAME");
        tableName                  = rs.getString("TABLE_NAME");
        columnName                 = rs.getString("COLUMN_NAME");
        referencedTableName        = rs.getString("REFERENCED_TABLE_NAME");
        referencedColumnName       = rs.getString("REFERENCED_COLUMN_NAME");
        ordinalPosition            = rs.getLong("ORDINAL_POSITION");
        positionInUniqueConstraint = rs.getLong("POSITION_IN_UNIQUE_CONSTRAINT");

        DBColumn c1 = ofNullable(tables.get(tableName)).map(t -> t.getColumn(columnName)).orElse(null);
        DBColumn c2 = ofNullable(tables.get(referencedTableName)).map(t -> t.getColumn(referencedColumnName)).orElse(null);

        if((c1 != null) && (c2 != null)) {
            column           = c1;
            referencedColumn = c2;
            type             = (c2.isUniquePrimaryKey() ? (c1.isUniquePrimaryKey() ? ForeignKeyType.OneToOne : ForeignKeyType.OneToMany) : ForeignKeyType.ManyToMany);

            c1.getTable().addReferencedForeignKey(this);
            c2.getTable().addReferencingForeignKey(this);

            switch(type) {
                case OneToOne -> {
                    c1.setToOneColumn(c2);
                    c2.setToOneColumn(c1);
                }
                case OneToMany -> {
                    c1.setToOneColumn(c2);
                    c2.addToManyColumn(c1);
                }
                case ManyToMany -> {
                    c1.addToManyColumn(c2);
                    c2.addToManyColumn(c1);
                }
            }
        }
        else {
            type             = ForeignKeyType.Unknown;
            column           = null;
            referencedColumn = null;
        }
    }

    public DBColumn getColumn() {
        return column;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public long getOrdinalPosition() {
        return ordinalPosition;
    }

    public long getPositionInUniqueConstraint() {
        return positionInUniqueConstraint;
    }

    public DBColumn getReferencedColumn() {
        return referencedColumn;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public String getTableName() {
        return tableName;
    }

    public ForeignKeyType getType() {
        return type;
    }

    public boolean isValid() {
        return ((column != null) && (referencedColumn != null));
    }

    public @Override String toString() {
        return "%s.%s %s %s.%s".formatted(tableName, columnName, switch(type) {
            case Unknown -> "<-X->";
            case OneToOne -> "<--->";
            case OneToMany -> "<--->>";
            case ManyToMany -> "<<--->>";
        }, referencedTableName, referencedColumnName);
    }

    public enum ForeignKeyType {OneToOne, OneToMany, ManyToMany, Unknown}
}
