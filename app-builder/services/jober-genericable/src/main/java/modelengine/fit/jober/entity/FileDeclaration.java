/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.entity;

/**
 * FileDeclaration
 *
 * @author 梁子涵
 * @since 2024/01/10
 */
public class FileDeclaration {
    private String name;

    private byte[] content;

    /**
     * FileDeclaration
     */
    public FileDeclaration() {

    }

    public FileDeclaration(String name, byte[] content) {
        this.content = content;
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
