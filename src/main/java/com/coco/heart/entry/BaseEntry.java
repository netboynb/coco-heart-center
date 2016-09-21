package com.coco.heart.entry;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.coco.heart.common.NoNullFieldStringStyle;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年3月25日 上午11:31:48
 * @func
 */
public class BaseEntry {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, new NoNullFieldStringStyle());
    }
}
