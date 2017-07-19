package cn.com.cis.common.entity;

import cn.com.cis.common.entity.plugin.LogicDeleteable;

import java.io.Serializable;

/**
 *
 * 逻辑删除entity
 *
 * @author David Kong
 * @version 1.0
 */
public abstract class LogicDeleteEntity<ID extends Serializable> extends BaseEntity<ID> implements LogicDeleteable {

    private static final long serialVersionUID = -2430227740098695178L;

    /**
     * 是否已逻辑删除
     */
    private Boolean deleted = Boolean.FALSE;

    @Override
    public Boolean getDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public void markDeleted() {
        setDeleted(Boolean.TRUE);
    }
}
