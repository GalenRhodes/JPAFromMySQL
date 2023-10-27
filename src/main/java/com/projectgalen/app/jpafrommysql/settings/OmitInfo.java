package com.projectgalen.app.jpafrommysql.settings;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: OmitInfo.java
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projectgalen.app.jpafrommysql.settings.FieldReference.FieldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OmitInfo {
    protected @JsonProperty("tables") Set<TableReference> tables = new TreeSet<>();
    protected @JsonProperty("fields") Set<FieldReference> fields = new TreeSet<>();

    public OmitInfo() { }

    public OmitInfo(Set<TableReference> tables, Set<FieldReference> fields) {
        this.tables = tables;
        this.fields = fields;
    }

    public @Override boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof OmitInfo omitInfo)) return false;
        return Objects.equals(tables, omitInfo.tables) && Objects.equals(fields, omitInfo.fields);
    }

    public Set<FieldReference> getFields() {
        return Collections.unmodifiableSet(fields);
    }

    public Set<TableReference> getTables() {
        return Collections.unmodifiableSet(tables);
    }

    public boolean has(@NotNull String schema, @NotNull String table) {
        return tables.stream().anyMatch(t -> t.matches(schema, table));
    }

    public boolean has(@NotNull String schema, @NotNull String table, @NotNull String field) {
        return fields.stream().anyMatch(f -> f.matches(schema, table, field));
    }

    public @Override int hashCode() {
        return Objects.hash(tables, fields);
    }

    public void setFields(Set<FieldReference> fields) {
        this.fields = new TreeSet<>(fields);
    }

    public void setTables(Set<TableReference> tables) {
        this.tables = new TreeSet<>(tables);
    }

    public @NotNull Stream<FieldInfo> streamFields() {
        Stream<FieldInfo> bigStream = Stream.empty();
        for(FieldReference field : fields) bigStream = Stream.concat(bigStream, field.stream());
        return bigStream;
    }

    public @NotNull Stream<TableReference> streamTables() {
        return tables.stream();
    }
}
