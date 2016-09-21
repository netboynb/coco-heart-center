package com.coco.heart.common;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 下午3:45:37
 * @func
 */
public class NoNullFieldStringStyle extends ToStringStyle {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoNullFieldStringStyle.class);
    private static final long serialVersionUID = -6521970798918055233L;

    public NoNullFieldStringStyle() {
        super();
        this.setUseShortClassName(true);
        this.setUseIdentityHashCode(false);
    }

    @Override
    public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            if (StringUtils.isEmpty((String) value)) {
                return;
            }
        } else if (value instanceof Date) {
            value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(value);
        }


        value = format(value);
        if (value == null) {
            return;
        }

        super.append(buffer, fieldName, value, fullDetail);
    }

    private Object format(Object value) {
        StringBuilder sb = new StringBuilder();
        format(value, sb);
        return sb.length() == 0 ? null : sb;
    }

    private void format(Object object, StringBuilder sb) {
        if (object == null) {
            sb.append("null");
            return;
        }

        if (object instanceof Map<?, ?>) {
            formatMap((Map<?, ?>) object, sb);
        } else if (object instanceof Iterable<?>) {
            formatCollection((Collection<?>) object, sb);
        } else if (object.getClass().isArray()) {
            formatArray(object, sb);
        } else if (object.getClass() == Object.class) {
            sb.append(object);
        } else {

            Method toStringMethod = null;
            Class<? extends Object> clazz = object.getClass();
            try {
                toStringMethod = clazz.getMethod("toString");
            } catch (Exception e) {
                LOGGER.error("can not find " + clazz.getName() + "from " + clazz.toString());
            }
            if (toStringMethod == null || toStringMethod.getDeclaringClass() == Object.class) {
                object = ToStringBuilder.reflectionToString(object);
            }
            sb.append(object);
        }
    }

    private <K, V> void formatMap(Map<K, V> map, StringBuilder sb) {
        if (((Map<?, ?>) map).size() == 0) {
            return;
        }

        boolean first = true;
        sb.append('{');
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            K key = entry.getKey();
            V value = entry.getValue();
            sb.append(key).append('=');
            format(value, sb);
        }
        sb.append('}');
    }

    private void formatArray(Object array, StringBuilder sb) {
        if (Array.getLength(array) == 0) {
            return;
        }

        int length = Array.getLength(array);
        sb.append('[');
        for (int i = 0; i < length; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            Object object = Array.get(array, i);
            format(object, sb);
        }
        sb.append(']');
    }

    private <T> void formatCollection(Collection<T> collection, StringBuilder sb) {
        if (collection.size() == 0) {
            return;
        }

        boolean first = true;
        sb.append('[');
        for (T t : collection) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            format(t, sb);
        }
        sb.append(']');
    }
}
