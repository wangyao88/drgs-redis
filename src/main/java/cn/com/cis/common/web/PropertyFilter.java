package cn.com.cis.common.web;

import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author David Kong
 * @version 1.0
 */
public class PropertyFilter {

    private final static Logger logger = LoggerFactory.getLogger(PropertyFilter.class);

    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 多个属性间OR关系的分隔符.
     */
    public static final String OR_SEPARATOR = "_OR_";

    /**
     * 从request对象中提取组装分页和排序对象，参数列表：
     * rows 每页记录数，默认20
     * page 当前页码数，从1开始，默认为1
     * start 起始记录顺序号，从1开始，用于一些需要精确控制从start到start+size的场景；可选参数，未提供则取值等于page*size
     * sidx 排序属性名称
     * sord 排序规则，asc=升序，desc=降序，默认asc
     *
     * @param request HttpServletRequest对象
     * @return
     */
    public static Page buildPageableFromHttpRequest(HttpServletRequest request) {
        return buildPageableFromHttpRequest(request, DEFAULT_PAGE_SIZE);
    }

    public static Page buildPageableFromHttpRequest(HttpServletRequest request, int defaultRows) {
        int pageSize = defaultRows;
        String paramRows = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(paramRows)) {
            int pr = Integer.valueOf(paramRows);
            if (pr <= 0) {
                return null;
            } else {
                pageSize = pr;
            }
        }
        int pageNum = 1;
        String strPage = request.getParameter("pageNum");
        if (StringUtils.isNotBlank(strPage)) {
            pageNum = Integer.valueOf(strPage);
        }
        return new Page(pageNum, pageSize);

    }

    public static <T> T buildEntityFromHttpRequest(Class<T> clazz, HttpServletRequest request) {
        Map<String, String[]> filterParamMap = getParametersStartingWith(request, "search['", "']");
        T obj = null;
        try {
            obj = clazz.newInstance();
            for (Map.Entry<String, String[]> entry : filterParamMap.entrySet()) {
                String filterName = entry.getKey();
                String[] values = entry.getValue();
                if (values == null || values.length == 0) {
                    continue;
                }
                String[] propertyNames = StringUtils.splitByWholeSeparator(filterName, PropertyFilter.OR_SEPARATOR);
                Method getMethod = null;
                Method setMethod = null;
                Class propertyClass = null;
                for (String propertyName : propertyNames) {
                    getMethod = MethodUtils.getAccessibleMethod(clazz, "get" + StringUtils.capitalize(propertyName));
                    setMethod = MethodUtils.getAccessibleMethod(clazz,"set"+StringUtils.capitalize(propertyName));
                    propertyClass = getMethod.getReturnType();
                }
                if (values.length == 1) {
                    String value = values[0];
                    // 如果value值为空,则忽略此filter.
                    if (StringUtils.isNotBlank(value) && setMethod != null) {
                        if (propertyClass.equals(int.class) || propertyClass.equals(Integer.class)) {
                            setMethod.invoke(obj, Integer.valueOf(value));
                        } else if (propertyClass.equals(boolean.class) ||
                                propertyClass.equals(Boolean.class)) {
                            setMethod.invoke(obj, Boolean.valueOf(value));
                        } else if (propertyClass.equals(String.class)) {
                            setMethod.invoke(obj, value);
                        } else if (propertyClass.equals(float.class) ||
                                propertyClass.equals(Float.class)) {
                            setMethod.invoke(obj, Float.valueOf(value));
                        } else if (propertyClass.equals(long.class) ||
                                propertyClass.equals(Long.class)) {
                            setMethod.invoke(obj, Long.valueOf(value));
                        } else if (propertyClass.equals(Date.class)) {
                            setMethod.invoke(obj, DateUtils.parseDate(value, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static Map<String, String[]> getParametersStartingWith(ServletRequest request, String prefix, String suffix) {
        Assert.notNull(request, "Request must not be null");
        @SuppressWarnings("rawtypes")
        Enumeration paramNames = request.getParameterNames();
        Map<String, String[]> params = new TreeMap<String, String[]>();
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (("".equals(prefix) || paramName.startsWith(prefix)) && ("".equals(suffix) || paramName.endsWith(suffix))) {
                String unprefixed = paramName.substring(prefix.length(), paramName.length() - suffix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, values);
                } else {
                    params.put(unprefixed, new String[]{values[0]});
                }
            }
        }
        return params;
    }
}
