/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package cn.com.cis.common.entity.plugin;

/**
 * <p>实体实现该接口表示想要逻辑删除
 * 为了简化开发 约定删除标识列名为deleted，如果想自定义删除的标识列名：
 * <p/>
 * <p>User: David Kong
 * <p>Date: 2015-11-05
 * <p>Version: 1.0
 */
public interface LogicDeleteable {

    Boolean getDeleted();

    void setDeleted(Boolean deleted);

    /**
     * 标识为已删除
     */
    void markDeleted();

}
