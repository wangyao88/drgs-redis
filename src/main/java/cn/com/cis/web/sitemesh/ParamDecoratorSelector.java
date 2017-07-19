package cn.com.cis.web.sitemesh;

import org.apache.commons.lang3.StringUtils;
import org.sitemesh.DecoratorSelector;
import org.sitemesh.content.Content;
import org.sitemesh.webapp.WebAppContext;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 基于request的header和parameter参数decorator值进行动态定位装饰器的选择器
 * 如果decorator参数有值，则返回"/WEB-INF/views/layouts/" + decorator + ".jsp"作为目标装饰模板页面
 * @author David Kong
 * @version 1.0
 */

public class ParamDecoratorSelector implements DecoratorSelector<WebAppContext> {

    private DecoratorSelector<WebAppContext> defaultDecoratorSelector;

    public ParamDecoratorSelector(DecoratorSelector<WebAppContext> defaultDecoratorSelector) {
        this.defaultDecoratorSelector = defaultDecoratorSelector;
    }

    public String[] selectDecoratorPaths(Content content, WebAppContext context) throws IOException {
        // build decorator based on the request
        HttpServletRequest request = context.getRequest();
        String decorator = null;
        //首先从header头信息取值
        decorator = request.getHeader("decorator");
        if (StringUtils.isBlank(decorator)) {
            //未取到再从参数取值
            decorator = request.getParameter("decorator");
        }
        if (StringUtils.isNotBlank(decorator)) {
            //按照参数值返回对应路径下面的jsp装饰模板页码
            return new String[] { "/WEB-INF/views/layouts/" + decorator + ".jsp" };
        }

        // Otherwise, fallback to the standard configuration
        return defaultDecoratorSelector.selectDecoratorPaths(content, context);
    }
}