/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import modelengine.fit.jane.task.util.UndefinableValue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为 {@link File} 提供默认实现。
 *
 * @author 陈镕希
 * @since 2023-10-10
 */
public class DefaultFile extends AbstractDomainObject implements File {
    private final String name;

    private final byte[] content;

    /**
     * 默认文件构造方法
     *
     * @param id 表示默认文件唯一标识的 {@link String}。
     * @param name 表示默认文件名称的 {@link String}。
     * @param content 表示默认文件context的 {@link byte[]}。
     * @param creator 表示默认文件创建者的 {@link String}。
     * @param creationTime 表示默认文件创建时间的 {@link LocalDateTime}。
     * @param lastModifier 表示默认文件上次更新者的 {@link String}。
     * @param lastModificationTime 表示默认文件上次更新时间的 {@link LocalDateTime}。
     */
    public DefaultFile(String id, String name, byte[] content, String creator, LocalDateTime creationTime,
            String lastModifier, LocalDateTime lastModificationTime) {
        super(id, creator, creationTime, lastModifier, lastModificationTime);
        this.name = name;
        this.content = content;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public byte[] content() {
        return this.content;
    }

    static class Declaration implements File.Declaration {
        private final UndefinableValue<String> name;

        private final UndefinableValue<byte[]> content;

        Declaration(UndefinableValue<String> name, UndefinableValue<byte[]> content) {
            this.name = name;
            this.content = content;
        }

        @Override
        public UndefinableValue<String> name() {
            return this.name;
        }

        @Override
        public UndefinableValue<byte[]> content() {
            return this.content;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof Declaration) {
                Declaration another = (Declaration) obj;
                return Objects.equals(this.name, another.name) && Objects.equals(this.content, another.content);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {this.getClass(), this.name, this.content});
        }

        @Override
        public String toString() {
            Map<String, Object> values = new HashMap<>(1);
            this.name.ifDefined(value -> values.put("name", value));
            this.content.ifDefined(value -> values.put("content", value));
            return values.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(", ", "[", "]"));
        }

        static class Builder implements File.Declaration.Builder {
            private UndefinableValue<String> name;

            private UndefinableValue<byte[]> content;

            Builder() {
                this.name = UndefinableValue.undefined();
                this.content = UndefinableValue.undefined();
            }

            @Override
            public File.Declaration.Builder name(String name) {
                this.name = UndefinableValue.defined(name);
                return this;
            }

            @Override
            public File.Declaration.Builder content(byte[] content) {
                this.content = UndefinableValue.defined(content);
                return this;
            }

            @Override
            public File.Declaration build() {
                return new Declaration(this.name, this.content);
            }
        }
    }

    static class Builder extends AbstractDomainObjectBuilder<File, File.Builder> implements File.Builder {
        private String name;

        private byte[] content;

        @Override
        public File.Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public File.Builder content(byte[] content) {
            this.content = content;
            return this;
        }

        @Override
        public File build() {
            return new DefaultFile(this.id(), this.name, this.content, this.creator(), this.creationTime(),
                    this.lastModifier(), this.lastModificationTime());
        }
    }
}
