package com.projectgalen.app.settings;

// ===========================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: ProjectInfo.java
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
public class ProjectInfo {
    private @JsonProperty("projectName")  String projectName;
    private @JsonProperty("author")       String author;
    private @JsonProperty("organization") String organization;

    public ProjectInfo() { }

    public ProjectInfo(boolean dummy) {
        projectName  = "";
        author       = "";
        organization = "";
    }

    public @Override boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ProjectInfo that)) return false;
        return Objects.equals(projectName, that.projectName) && Objects.equals(author, that.author) && Objects.equals(organization, that.organization);
    }

    public String getAuthor() {
        return author;
    }

    public String getOrganization() {
        return organization;
    }

    public String getProjectName() {
        return projectName;
    }

    public @Override int hashCode() {
        return Objects.hash(projectName, author, organization);
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
