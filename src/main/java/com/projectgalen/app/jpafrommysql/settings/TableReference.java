package com.projectgalen.app.jpafrommysql.settings;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: TableReference.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 27, 2023
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.projectgalen.app.jpafrommysql.Utils.getRegex;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableReference implements Comparable<TableReference> {
    protected @JsonIgnore                                      Pattern schemaRegex = null;
    protected @JsonIgnore                                      Pattern tableRegex  = null;
    protected @JsonProperty(value = "schema", required = true) String  schema;
    protected @JsonProperty(value = "table", required = true)  String  table;

    public TableReference() { }

    public TableReference(@NotNull String schema, @NotNull String table) {
        this.schema = schema;
        this.table  = table;
    }

    public @Override int compareTo(@NotNull TableReference o) {
        int cc = schema.compareTo(o.schema);
        return ((cc != 0) ? cc : table.compareTo(o.table));
    }

    public @Override boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof TableReference that)) return false;
        return Objects.equals(schema, that.schema) && Objects.equals(table, that.table);
    }

    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    public @Override int hashCode() {
        return Objects.hash(schema, table);
    }

    public boolean matches(@NotNull String schema, @NotNull String table) {
        return (getSchemaRegex().matcher(schema).matches() && getTableRegex().matcher(table).matches());
    }

    public void setSchema(String schema) {
        this.schema      = schema;
        this.schemaRegex = null;
    }

    public void setTable(String table) {
        this.table      = table;
        this.tableRegex = null;
    }

    protected synchronized Pattern getSchemaRegex() {
        if(schemaRegex == null) schemaRegex = getRegex(this.schema);
        return schemaRegex;
    }

    protected synchronized Pattern getTableRegex() {
        if(tableRegex == null) tableRegex = getRegex(this.table);
        return tableRegex;
    }
}
