package com.projectgalen.app.jpafrommysql.settings.app;
// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: AppSettings.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 29, 2023
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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projectgalen.app.jpafrommysql.JPAFromMySQL;
import com.projectgalen.lib.utils.json.JsonTools;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppSettings {

    public static final String appSettingsFilename = String.join(File.separator, System.getProperty("user.home"), JPAFromMySQL.props.getProperty("app.settings.filename"));

    protected @JsonProperty("windowWidth")    int    windowWidth;
    protected @JsonProperty("windowHeight")   int    windowHeight;
    protected @JsonProperty("windowX")        int    windowX;
    protected @JsonProperty("windowY")        int    windowY;
    protected @JsonProperty("lastFileOpened") String lastFileOpened;

    public AppSettings() { }

    public String getLastFileOpened() {
        return lastFileOpened;
    }

    public @JsonIgnore @NotNull Rectangle getWindowBounds() {
        return new Rectangle(windowX, windowY, windowWidth, windowHeight);
    }

    public @NotNull Rectangle getWindowBounds(@NotNull Rectangle b) {
        b.setBounds(windowX, windowY, windowWidth, windowHeight);
        return b;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public @JsonIgnore @NotNull Point getWindowLocation() {
        return new Point(windowX, windowY);
    }

    public @JsonIgnore @NotNull Dimension getWindowSize() {
        return new Dimension(windowWidth, windowHeight);
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowX() {
        return windowX;
    }

    public int getWindowY() {
        return windowY;
    }

    public AppSettings save() {
        try {
            JsonTools.getObjectMapper().writeValue(new File(appSettingsFilename), this);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(JPAFromMySQL.app(), "Error while saving app settings: %s".formatted(e), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return this;
    }

    public void setLastFileOpened(String lastFileOpened) {
        this.lastFileOpened = lastFileOpened;
    }

    public @JsonIgnore void setWindowBounds(@NotNull Rectangle b) {
        windowX      = b.x;
        windowY      = b.y;
        windowWidth  = b.width;
        windowHeight = b.height;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setWindowX(int windowX) {
        this.windowX = windowX;
    }

    public void setWindowY(int windowY) {
        this.windowY = windowY;
    }

    public static AppSettings load() {
        try {
            return JsonTools.getObjectMapper().readValue(new File(appSettingsFilename), AppSettings.class);
        }
        catch(Exception e) {
            return new AppSettings().save();
        }
    }
}
