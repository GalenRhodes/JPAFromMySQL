package com.projectgalen.app.jpafrommysql.dbinfo.innodb;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: FKType.java
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FKType {
    //0 = ON DELETE/UPDATE RESTRICT, 1 = ON DELETE CASCADE, 2 = ON DELETE SET NULL, 4 = ON UPDATE CASCADE, 8 = ON UPDATE SET NULL, 16 = ON DELETE NO ACTION, 32 = ON UPDATE NO ACTION
    RESTRICT(0), ON_DELETE_CASCADE(1), ON_DELETE_SET_NULL(2), ON_UPDATE_CASCADE(4), ON_UPDATE_SET_NULL(8), ON_DELETE_NO_ACTION(16), ON_UPDATE_NO_ACTION(32);

    private final int id;

    FKType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isFlagSet(int flags) {
        return ((flags & id) == id);
    }

    @Contract(pure = true) public static int combine(@NotNull Collection<FKType> types) {
        return Stream.of(values()).mapToInt(FKType::getId).reduce(0, (a, b) -> (a | b));
    }

    public static @NotNull Set<FKType> getTypes(int flags) {
        return Stream.of(values()).filter(t -> t.isFlagSet(flags)).collect(Collectors.toSet());
    }
}
