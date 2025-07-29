/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.domain;

import java.time.LocalDateTime;

/**
 * 领域对象构建器
 *
 * @author 梁济时
 * @since 2023-09-12
 */
public abstract class AbstractDomainObjectBuilder<T extends DomainObject, B extends DomainObject.Builder<T, B>>
        implements DomainObject.Builder<T, B> {
    private String id;

    private String creator;

    private LocalDateTime creationTime;

    private String lastModifier;

    private LocalDateTime lastModificationTime;

    /**
     * self
     *
     * @return B
     */
    protected final B self() {
        return (B) this;
    }

    @Override
    public B id(String id) {
        this.id = id;
        return this.self();
    }

    @Override
    public B creator(String creator) {
        this.creator = creator;
        return this.self();
    }

    @Override
    public B creationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
        return this.self();
    }

    @Override
    public B lastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
        return this.self();
    }

    @Override
    public B lastModificationTime(LocalDateTime lastModificationTime) {
        this.lastModificationTime = lastModificationTime;
        return this.self();
    }

    protected String id() {
        return this.id;
    }

    protected String creator() {
        return this.creator;
    }

    protected LocalDateTime creationTime() {
        return this.creationTime;
    }

    protected String lastModifier() {
        return this.lastModifier;
    }

    protected LocalDateTime lastModificationTime() {
        return this.lastModificationTime;
    }
}
