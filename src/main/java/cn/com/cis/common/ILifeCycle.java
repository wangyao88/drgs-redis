package cn.com.cis.common;

/**
 * 组件生命周期定义。提供初始化和销毁两个方法。
 * 
 */
public interface ILifeCycle {

    /**
     * 初始化资源。
     */
    public void init() throws Exception;
    
    /**
     * 释放资源。
     */
    public void destroy() throws Exception;

}
