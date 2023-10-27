package com.projectgalen.app.jpafrommysql.settings;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: FieldReference.java
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
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.projectgalen.app.jpafrommysql.Utils.getRegex;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldReference implements Comparable<FieldReference> {

    protected @JsonIgnore                                      List<Pattern> fieldRegex  = null;
    protected @JsonIgnore                                      Pattern       schemaRegex = null;
    protected @JsonIgnore                                      Pattern       tableRegex  = null;
    protected @JsonProperty(value = "schema", required = true) String        schema;
    protected @JsonProperty(value = "table", required = true)  String        table;
    protected @JsonProperty("fields")                          Set<String>   fields      = new TreeSet<>();

    public FieldReference() { }

    public FieldReference(@NotNull String schema, @NotNull String table, String... fields) {
        this.fields = new TreeSet<>(List.of(fields));
    }

    public @JsonIgnore void addField(@NotNull String field) {
        fields.add(field);
    }

    public void clearFields() {
        fields.clear();
    }

    public @Override int compareTo(@NotNull FieldReference o) {
        int cc = schema.compareTo(o.schema);
        if(cc != 0) return cc;
        if((cc = table.compareTo(o.table)) != 0) return cc;
        if((cc = Integer.compare(fields.size(), o.fields.size())) != 0) return cc;
        List<String> a = new ArrayList<>(fields);
        List<String> b = new ArrayList<>(o.fields);
        for(int i = 0, j = a.size(); i < j; i++) if((cc = a.get(i).compareTo(b.get(i))) != 0) return cc;
        return 0;
    }

    public @JsonIgnore int count() {
        return fields.size();
    }

    public @Override boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof FieldReference that)) return false;
        return Objects.equals(schema, that.schema) && Objects.equals(table, that.table) && Objects.equals(fields, that.fields);
    }

    public Set<String> getFields() {
        return fields;
    }

    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    public @Override int hashCode() {
        return Objects.hash(schema, table, fields);
    }

    public boolean matches(@NotNull String schema, @NotNull String table, @NotNull String field) {
        return (getSchemaRegex().matcher(schema).matches() && getTableRegex().matcher(table).matches() && getFieldRegex().stream().anyMatch(r -> r.matcher(field).matches()));
    }

    public @JsonIgnore void removeField(@NotNull String field) {
        fields.remove(field);
    }

    public void setFields(@NotNull Set<String> fields) {
        this.fields = new TreeSet<>(fields);
    }

    public void setSchema(String schema) {
        this.schema      = schema;
        this.schemaRegex = null;
    }

    public void setTable(String table) {
        this.table      = table;
        this.tableRegex = null;
    }

    public @JsonIgnore @NotNull Stream<FieldInfo> stream() {
        return fields.stream().map(field -> new FieldInfo(schema, table, field));
    }

    protected synchronized List<Pattern> getFieldRegex() {
        if(fieldRegex == null) fieldRegex = fields.stream().map(Utils::getRegex).toList();
        return fieldRegex;
    }

    protected synchronized Pattern getSchemaRegex() {
        if(schemaRegex == null) schemaRegex = getRegex(this.schema);
        return schemaRegex;
    }

    protected synchronized Pattern getTableRegex() {
        if(tableRegex == null) tableRegex = getRegex(this.table);
        return tableRegex;
    }

    public record FieldInfo(@NotNull String schema, @NotNull String table, @NotNull String field) { }
}
