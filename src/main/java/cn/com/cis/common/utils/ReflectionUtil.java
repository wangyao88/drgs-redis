package cn.com.cis.common.utils;

import cn.com.cis.utils.Exceptions;
import lombok.extern.log4j.Log4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射操作常用方法。
 */
@Log4j
public class ReflectionUtil {

    /**
     * 生成指定完整类名的实例。
     *
     * @param className 完整类名
     * @return 类实例。如果找不到指定的类名，将返回false。
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String className) {
        if (null == className) {
            return null;
        }

        T listener = null;
        try {
            Class<T> listenerClass = (Class<T>) Class.forName(className);
            Constructor<T> constructor = listenerClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            listener = constructor.newInstance();
        } catch (ClassNotFoundException e) {
            log.error(String.format("could not found class:%s", className), e);
        } catch (Exception e) {
            log.error(String.format("create instance for class:%s fail", className), e);
        }

        return listener;
    }

    /**
     * 生成指定完整类名的实例。
     *
     * @param className      完整类名
     * @param parameterTypes 构造参数类型数组
     * @param initargs       调用构造方法生成实例时作为参数传给构造方法的对象数组
     * @return 类实例。如果找不到指定的类名，将返回false。
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String className,
                                       Class<?>[] parameterTypes, Object[] initargs) {
        if (null == className) {
            return null;
        }

        T listener = null;
        try {
            Class<T> listenerClass = (Class<T>) Class.forName(className);
            Constructor<T> constructor = listenerClass.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            listener = constructor.newInstance(initargs);
        } catch (ClassNotFoundException e) {
            log.error(String.format("could not found class:%s", className), e);
        } catch (Exception e) {
            log.error(String.format("create instance for class:%s fail", className), e);
        }

        return listener;
    }

    /**
     * 对象属性拷贝
     *
     * @param from
     * @param to
     */
    public static void fieldsCopy(Object from, Object to) {
        Field[] fields = from.getClass().getDeclaredFields();

        for (Field field : fields) {
            String setterMethodName = "set" + StringUtil.makeInitialUpperCase(field.getName());
            String getterMethodName = "get" + StringUtil.makeInitialUpperCase(field.getName());

            try {
                Method getterMethod = getDeclaredMethod(from, getterMethodName);
                Method setterMethod = getMethodByName(to, setterMethodName);

                Object fieldValue = getterMethod.invoke(from);
                setterMethod.invoke(to, fieldValue.getClass().cast(fieldValue));
            } catch (NullPointerException e) {
                log.info("反射方法未找到!" + Exceptions.getStackTraceAsString(e));
            } catch (InvocationTargetException e) {
                log.info("反射方法调用失败!" + Exceptions.getStackTraceAsString(e));
            } catch (IllegalAccessException e) {
                log.info("反射方法不能被访问!" + Exceptions.getStackTraceAsString(e));
            }
        }
    }

    /**
     * 查找对象上的方法
     *
     * @param object
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;

        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (NoSuchMethodException e) {
                log.info("反射方法未找到!" + Exceptions.getStackTraceAsString(e));
            }
        }

        return null;
    }

    /**
     * 找到对象中名称为methodName的方法, 适用于不关心参数类型的场合
     * @param object
     * @param methodName
     * @return
     */
    public static Method getMethodByName(Object object, String methodName) {

        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Method methodInClazz : clazz.getDeclaredMethods()) {
                if (methodInClazz.getName().equals(methodName)) {
                    return methodInClazz;
                }
            }
        }

        return null;
    }

}
