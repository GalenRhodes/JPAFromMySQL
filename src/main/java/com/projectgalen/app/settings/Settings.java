package com.projectgalen.app.settings;

// ===========================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: Settings.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: October 23, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Settings {

    private @JsonProperty("saveSettings")     boolean        saveSettings;
    private @JsonProperty("settingsSavePath") String         settingsSavePath;
    private @JsonProperty("serverInfo")       ServerInfo     serverInfo;
    private @JsonProperty("projectInfo")      ProjectInfo    projectInfo;
    private @JsonProperty("generationInfo")   GenerationInfo generationInfo;

    public Settings() { }

    public Settings(boolean dummy) {
        serverInfo       = new ServerInfo(dummy);
        projectInfo      = new ProjectInfo(dummy);
        generationInfo   = new GenerationInfo(dummy);
        saveSettings     = true;
        settingsSavePath = "./MySQL2JPA_config.json";
    }

    public @Override boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Settings settings)) return false;
        return (saveSettings == settings.saveSettings)/*@f0*/
               && Objects.equals(settingsSavePath, settings.settingsSavePath)
               && Objects.equals(serverInfo, settings.serverInfo)
               && Objects.equals(projectInfo, settings.projectInfo)
               && Objects.equals(generationInfo, settings.generationInfo);/*@f1*/
    }

    public GenerationInfo getGenerationInfo() {
        return generationInfo;
    }

    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public String getSettingsSavePath() {
        return settingsSavePath;
    }

    public @Override int hashCode() {
        return Objects.hash(serverInfo, projectInfo, generationInfo);
    }

    public boolean isSaveSettings() {
        return saveSettings;
    }

    public void setGenerationInfo(GenerationInfo generationInfo) {
        this.generationInfo = generationInfo;
    }

    public void setProjectInfo(ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    public void setSaveSettings(boolean saveSettings) {
        this.saveSettings = saveSettings;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public void setSettingsSavePath(String settingsSavePath) {
        this.settingsSavePath = settingsSavePath;
    }
}
