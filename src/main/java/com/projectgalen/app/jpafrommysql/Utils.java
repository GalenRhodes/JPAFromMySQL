package com.projectgalen.app.jpafrommysql;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: Utils.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 25, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ================================================================================================================================

import com.formdev.flatlaf.util.SystemInfo;
import com.projectgalen.app.jpafrommysql.settings.Settings;
import com.projectgalen.lib.utils.json.JsonTools;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class Utils {

    private static final String CP = ("." + File.separator);

    private Utils() { }

    public static @NotNull String abbreviatePath(@NotNull String path) {
        String currentDir = (getCurrentDirectory().getPath() + File.separator);
        return (path.startsWith(currentDir) ? (CP + path.substring(currentDir.length())) : path);
    }

    public static void clearPassword(char[] pw) {
        if(pw != null) for(int i = 0; i < 1000; i++) Arrays.fill(pw, (char)(Math.random() * Character.MAX_VALUE));
    }

    public static @NotNull File getCurrentDirectory() {
        File file = new File(System.getProperty("user.dir"));
        try { return file.getCanonicalFile(); } catch(IOException e) { return file; }
    }

    public static @Contract("_, _, !null, _ -> !null") String getFromFileDialog(Component parent, @NotNull String title, @Nullable String initialPathname, boolean files) {
        return getFromFileDialog(parent, title, initialPathname, files, true);
    }

    public static @Contract("_, _, !null, _, _ -> !null") String getFromFileDialog(Component parent, @NotNull String title, @Nullable String initialPathname, boolean files, boolean abbreviate) {
        try {
            File         file    = Optional.ofNullable(initialPathname).map(File::new).map(File::getParentFile).orElseGet(Utils::getCurrentDirectory);
            JFileChooser chooser = new JFileChooser(file);
            chooser.setFileSelectionMode(files ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle(title);
            chooser.setSelectedFile(file);
            chooser.setMultiSelectionEnabled(false);
            if((files ? chooser.showSaveDialog(parent) : chooser.showOpenDialog(parent)) != JFileChooser.APPROVE_OPTION) return initialPathname;
            String output = chooser.getSelectedFile().getCanonicalPath();
            return (abbreviate ? abbreviatePath(output) : output);
        }
        catch(IOException e) {
            return initialPathname;
        }
    }

    public static @Nullable Long getLong(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
        long value = rs.getLong(column);
        return (rs.wasNull() ? null : value);
    }

    public static @NotNull Pattern getRegex(@NotNull String rstr) {
        return ((rstr.startsWith("/") && rstr.endsWith("/")) ? Pattern.compile(rstr.substring(1, rstr.length() - 1)) : Pattern.compile(Pattern.quote(rstr)));
    }

    public static Settings loadSettings(String filename) {
        try {
            return JsonTools.readJsonFile(filename, Settings.class, true);
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull String removePrefix(@NotNull String string, @NotNull String prefix) {
        return string.startsWith(prefix) ? string.substring(prefix.length()) : string;
    }

    public static @NotNull String unabbreviatePath(@NotNull String path) {
        try {
            String _path = path.strip();
            if(_path.isEmpty()) return getCurrentDirectory().getPath();

            if(SystemInfo.isWindows || SystemInfo.isWinPE) {
                if(_path.matches("(?is)^(\\\\|[a-z]:).*")) return _path;
            }
            else {
                if(_path.startsWith(File.separator)) return _path;
            }

            if(_path.startsWith(CP)) _path = _path.substring(CP.length());
            return new File(getCurrentDirectory(), _path).getCanonicalPath();
        }
        catch(IOException e) {
            return path;
        }
    }
}
