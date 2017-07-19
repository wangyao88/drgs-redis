package cn.com.cis.common.entity;

import cn.com.cis.common.entity.plugin.Treeable;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author David Kong
 * @version 1.0
 */
public abstract class TreeEntity<ID extends Serializable> extends BaseEntity<ID> implements Treeable<ID> {

    private static final long serialVersionUID = -6907994838472540014L;

    protected ID parentId;

    private String name;

    private String icon;

    private String parentIds;

    private Integer weight;

    private boolean hasChildren;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIcon() {
        if (!StringUtils.isEmpty(icon)) {
            return icon;
        }
        if (isRoot()) {
            return getRootDefaultIcon();
        }
        if (isLeaf()) {
            return getLeafDefaultIcon();
        }
        return getBranchDefaultIcon();
    }

    @Override
    public ID getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(ID parentId) {
        this.parentId = parentId;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getParentIds() {
        return parentIds;
    }

    @Override
    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public String makeSelfAsNewParentIds() {
        return getParentIds() + getId() + getSeparator();
    }

    @Override
    public Integer getWeight() {
        return weight;
    }

    @Override
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public boolean isRoot() {
        return null == getParentId();
    }

    @Override
    public boolean isLeaf() {
        return !isRoot() && !isHasChildren();
    }

    @Override
    public boolean isHasChildren() {
        return hasChildren;
    }

    /** 根节点默认图标 如果没有默认 空即可*/
    @Override
    public String getRootDefaultIcon() {
        return "";
    }

    /** 树枝节点默认图标 如果没有默认 空即可 */
    @Override
    public String getBranchDefaultIcon() {
        return "";
    }

    /** 树叶节点默认图标 如果没有默认 空即可*/
    @Override

    public String getLeafDefaultIcon() {
        return "";
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
