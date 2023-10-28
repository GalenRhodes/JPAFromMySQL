package com.projectgalen.app.jpafrommysql.settings;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: ColumnReference.java
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
import com.projectgalen.app.jpafrommysql.Utils;
import com.projectgalen.app.jpafrommysql.dbinfo.DBColumn;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.projectgalen.app.jpafrommysql.Utils.getRegex;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ColumnReference implements Comparable<ColumnReference> {

    protected @JsonIgnore                                      List<Pattern> columnRegex = null;
    protected @JsonIgnore                                      Pattern       schemaRegex = null;
    protected @JsonIgnore                                      Pattern       tableRegex  = null;
    protected @JsonProperty(value = "schema", required = true) String        schema;
    protected @JsonProperty(value = "table", required = true)  String        table;
    protected @JsonProperty("columns")                         Set<String>   columns     = new TreeSet<>();

    public ColumnReference() { }

    public ColumnReference(@NotNull String schema, @NotNull String table, String... columns) {
        this.columns = new TreeSet<>(List.of(columns));
    }

    public @JsonIgnore void addField(@NotNull String field) {
        columns.add(field);
        columnRegex = null;
    }

    public void clearFields() {
        columns.clear();
        columnRegex = null;
    }

    public @Override int compareTo(@NotNull ColumnReference o) {
        int cc = schema.compareTo(o.schema);
        if(cc != 0) return cc;
        if((cc = table.compareTo(o.table)) != 0) return cc;
        if((cc = Integer.compare(columns.size(), o.columns.size())) != 0) return cc;
        List<String> a = new ArrayList<>(columns);
        List<String> b = new ArrayList<>(o.columns);
        for(int i = 0, j = a.size(); i < j; i++) if((cc = a.get(i).compareTo(b.get(i))) != 0) return cc;
        return 0;
    }

    public @JsonIgnore int count() {
        return columns.size();
    }

    public @Override boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ColumnReference that)) return false;
        return Objects.equals(schema, that.schema) && Objects.equals(table, that.table) && Objects.equals(columns, that.columns);
    }

    public Set<String> getColumns() {
        return columns;
    }

    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    public @Override int hashCode() {
        return Objects.hash(schema, table, columns);
    }

    public boolean matches(@NotNull DBColumn column) {
        return matches(column.getTable().getTableSchema(), column.getTable().getTableName(), column.getColumnName());
    }

    public boolean matches(@NotNull String schema, @NotNull String table, @NotNull String column) {
        return (getSchemaRegex().matcher(schema).matches() && getTableRegex().matcher(table).matches() && getColumnRegex().stream().anyMatch(r -> r.matcher(column).matches()));
    }

    public @JsonIgnore void removeField(@NotNull String field) {
        columns.remove(field);
        columnRegex = null;
    }

    public void setColumns(@NotNull Set<String> columns) {
        this.columns = new TreeSet<>(columns);
        columnRegex  = null;
    }

    public void setSchema(String schema) {
        this.schema      = schema;
        this.schemaRegex = null;
    }

    public void setTable(String table) {
        this.table      = table;
        this.tableRegex = null;
    }

    public @JsonIgnore @NotNull Stream<FullColumnName> stream() {
        return columns.stream().map(field -> new FullColumnName(schema, table, field));
    }

    protected List<Pattern> getColumnRegex() {
        if(columnRegex == null) columnRegex = columns.stream().map(Utils::getRegex).toList();
        return columnRegex;
    }

    protected Pattern getSchemaRegex() {
        if(schemaRegex == null) schemaRegex = getRegex(this.schema);
        return schemaRegex;
    }

    protected Pattern getTableRegex() {
        if(tableRegex == null) tableRegex = getRegex(this.table);
        return tableRegex;
    }

    public record FullColumnName(@NotNull String schema, @NotNull String table, @NotNull String column) { }
}
