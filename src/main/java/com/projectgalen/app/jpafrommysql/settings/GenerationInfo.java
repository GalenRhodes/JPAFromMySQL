package com.projectgalen.app.jpafrommysql.settings;

// ================================================================================================================================
//     PROJECT: JPAFromMySQL
//    FILENAME: GenerationInfo.java
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerationInfo {
    private @JsonProperty("outputPath")      String         outputPath;
    private @JsonProperty("basePackage")     String         basePackage;
    private @JsonProperty("baseClassPrefix") String         baseClassPrefix;
    private @JsonProperty("baseClassSuffix") String         baseClassSuffix;
    private @JsonProperty("splitEntities")   boolean        splitEntities;
    private @JsonProperty("subclassPackage") String         subclassPackage;
    private @JsonProperty("subclassPrefix")  String         subclassPrefix;
    private @JsonProperty("subclassSuffix")  String         subclassSuffix;
    private @JsonProperty("fkPrefix")        String         fkPrefix;
    private @JsonProperty("fkSuffix")        String         fkSuffix;
    private @JsonProperty("fkToOnePattern")  String         fkToOnePattern;
    private @JsonProperty("fkToManyPattern") String         fkToManyPattern;
    private @JsonProperty("omitted")         List<OmitInfo> omitted = new ArrayList<>();

    public GenerationInfo() { }

    public GenerationInfo(boolean dummy) {
        outputPath      = "src/main/java";
        basePackage     = "com.jpa.base";
        baseClassPrefix = "_";
        baseClassSuffix = "Entity";
        splitEntities   = true;
        subclassPackage = "com.jpa";
        subclassPrefix  = "";
        subclassSuffix  = "Entity";
        fkPrefix        = "to";
        fkSuffix        = "";
        fkToOnePattern  = "^(.+?)Entity$";
        fkToManyPattern = "^(.+?)Id$";
    }

    public @Override boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof GenerationInfo that)) return false;
        return splitEntities == that.splitEntities/*@f0*/
               && Objects.equals(outputPath, that.outputPath)
               && Objects.equals(basePackage, that.basePackage)
               && Objects.equals(baseClassPrefix, that.baseClassPrefix)
               && Objects.equals(baseClassSuffix, that.baseClassSuffix)
               && Objects.equals(subclassPackage, that.subclassPackage)
               && Objects.equals(subclassPrefix, that.subclassPrefix)
               && Objects.equals(subclassSuffix, that.subclassSuffix)
               && Objects.equals(fkPrefix, that.fkPrefix)
               && Objects.equals(fkSuffix, that.fkSuffix)
               && Objects.equals(fkToOnePattern, that.fkToOnePattern)
               && Objects.equals(fkToManyPattern, that.fkToManyPattern);/*@f1*/
    }

    public String getBaseClassPrefix() {
        return baseClassPrefix;
    }

    public String getBaseClassSuffix() {
        return baseClassSuffix;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getFkPrefix() {
        return fkPrefix;
    }

    public String getFkSuffix() {
        return fkSuffix;
    }

    public String getFkToManyPattern() {
        return fkToManyPattern;
    }

    public String getFkToOnePattern() {
        return fkToOnePattern;
    }

    public List<OmitInfo> getOmitted() {
        return Collections.unmodifiableList(omitted);
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getSubclassPackage() {
        return subclassPackage;
    }

    public String getSubclassPrefix() {
        return subclassPrefix;
    }

    public String getSubclassSuffix() {
        return subclassSuffix;
    }

    public @Override int hashCode() {
        return Objects.hash(outputPath,
                            basePackage,
                            baseClassPrefix,
                            baseClassSuffix,
                            splitEntities,
                            subclassPackage,
                            subclassPrefix,
                            subclassSuffix,
                            fkPrefix,
                            fkSuffix,
                            fkToOnePattern,
                            fkToManyPattern);
    }

    public boolean isSplitEntities() {
        return splitEntities;
    }

    public void setBaseClassPrefix(String baseClassPrefix) {
        this.baseClassPrefix = baseClassPrefix;
    }

    public void setBaseClassSuffix(String baseClassSuffix) {
        this.baseClassSuffix = baseClassSuffix;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setFkPrefix(String fkPrefix) {
        this.fkPrefix = fkPrefix;
    }

    public void setFkSuffix(String fkSuffix) {
        this.fkSuffix = fkSuffix;
    }

    public void setFkToManyPattern(String fkToManyPattern) {
        this.fkToManyPattern = fkToManyPattern;
    }

    public void setFkToOnePattern(String fkToOnePattern) {
        this.fkToOnePattern = fkToOnePattern;
    }

    public void setOmitted(List<OmitInfo> omitted) {
        this.omitted = new ArrayList<>(omitted);
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setSplitEntities(boolean splitEntities) {
        this.splitEntities = splitEntities;
    }

    public void setSubclassPackage(String subclassPackage) {
        this.subclassPackage = subclassPackage;
    }

    public void setSubclassPrefix(String subclassPrefix) {
        this.subclassPrefix = subclassPrefix;
    }

    public void setSubclassSuffix(String subclassSuffix) {
        this.subclassSuffix = subclassSuffix;
    }
}
