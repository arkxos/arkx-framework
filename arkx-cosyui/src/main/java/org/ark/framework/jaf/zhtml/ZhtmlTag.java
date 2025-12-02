package org.ark.framework.jaf.zhtml;

import java.lang.reflect.Method;

import io.arkx.framework.commons.collection.CaseIgnoreMapx;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.NumberUtil;
import io.arkx.framework.commons.util.ObjectUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.IterationTag;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlTag
 *
 * @author Darkness
 * @date 2013-1-31 下午12:56:00
 * @version V1.0
 */
public class ZhtmlTag implements Cloneable {
    public String TagLibURI;
    public String TagName;
    public Mapx<String, JspTagAttribute> Attributes;
    public String TagClass;
    public Mapx<String, Method> MethodMap = new CaseIgnoreMapx<String, Method>();
    public Class<?> Clazz;
    private TagSupport tagSupport;
    private int startLineNo;
    private int startTagFlag;
    private JspWriter out;
    private ZhtmlTag parent;

    private void init() {
        if (this.Clazz == null)
            try {
                this.Clazz = Class.forName(this.TagClass);

                for (Method m : this.Clazz.getMethods()) {
                    String name = m.getName();

                    if (!name.startsWith("set")) {
                        continue;
                    }
                    if (m.getModifiers() != 1) {
                        continue;
                    }
                    if ((m.getParameterTypes() == null) || (m.getParameterTypes().length != 1)) {
                        continue;
                    }
                    name = name.substring(3);
                    for (String k : this.Attributes.keyArray())
                        if (k.equalsIgnoreCase(name))
                            this.MethodMap.put(k, m);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            }
    }

    public boolean hasAttribute(String key) {
        return this.Attributes.containsKey(key);
    }

    public boolean isValidAttributeValue(String attr, String value) throws ZhtmlCompileException {
        init();
        Method m = (Method) this.MethodMap.get(attr);
        if (m == null) {
            throw new ZhtmlCompileException("标签" + this.TagName + "没有对应的set方法:" + attr);
        }
        Class<?> cls = m.getParameterTypes()[0];
        if (cls == Integer.class)
            return NumberUtil.isInt(value);
        if (cls == Long.class)
            return NumberUtil.isLong(value);
        if (cls == Float.class)
            return NumberUtil.isFloat(value);
        if (cls == Double.class)
            return NumberUtil.isDouble(value);
        if (cls == Boolean.class) {
            return ObjectUtil.isBoolean(value);
        }
        return true;
    }

    public boolean isIterative() {
        init();

        return IterationTag.class.isAssignableFrom(this.Clazz);
    }

    public int getStartLineNo() {
        return this.startLineNo;
    }

    public void setAttribute(String attr, String value) throws ZhtmlRuntimeException {
        try {
            if (!isValidAttributeValue(attr, value))
                throw new ZhtmlRuntimeException("标签" + this.TagName + "的属性" + attr + "的值不是正确的类型：" + value);
        } catch (ZhtmlCompileException e) {
            throw new ZhtmlRuntimeException(e.getMessage());
        }
        Method m = (Method) this.MethodMap.get(attr);
        Class cls = m.getParameterTypes()[0];
        try {
            if ((cls == Integer.class) || (cls == Integer.TYPE))
                m.invoke(getTagSupport(), new Object[]{Integer.valueOf(Integer.parseInt(value))});
            else if ((cls == Long.class) || (cls == Long.TYPE))
                m.invoke(getTagSupport(), new Object[]{Long.valueOf(Long.parseLong(value))});
            else if ((cls == Float.class) || (cls == Float.TYPE))
                m.invoke(getTagSupport(), new Object[]{Float.valueOf(Float.parseFloat(value))});
            else if ((cls == Double.class) || (cls == Double.TYPE))
                m.invoke(getTagSupport(), new Object[]{Double.valueOf(Double.parseDouble(value))});
            else if ((cls == Boolean.class) || (cls == Boolean.TYPE))
                m.invoke(getTagSupport(), new Object[]{Boolean.valueOf("true".equals(value))});
            else
                m.invoke(getTagSupport(), new Object[]{value == null ? null : String.valueOf(value)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ZhtmlTag clone() {
        init();
        ZhtmlTag tag = new ZhtmlTag();
        tag.Attributes = this.Attributes;
        tag.Clazz = this.Clazz;
        tag.TagClass = this.TagClass;
        tag.TagLibURI = this.TagLibURI;
        tag.TagName = this.TagName;
        tag.MethodMap = this.MethodMap;
        return tag;
    }

    public void setStartLineNo(int startLineNo) {
        this.startLineNo = startLineNo;
    }

    public void setParent(ZhtmlTag tag) {
        this.tagSupport.setParent(tag.getTagSupport());
        this.parent = tag;
    }

    public void onEnter(ZhtmlPage page) throws ZhtmlRuntimeException {
        try {
            this.startTagFlag = this.tagSupport.doStartTag();
        } catch (JspException e) {
            e.printStackTrace();
            throw new ZhtmlRuntimeException(e.getMessage());
        }
    }

    public int getStartTagFlag() {
        return this.startTagFlag;
    }

    public void onExit(ZhtmlPage page) {
        if (this.parent != null) {
            page.setCurrentTag(this.parent);
        }
        this.tagSupport.release();
    }

    public void afterBody(ZhtmlPage page) {
        if ((this.startTagFlag != 0) && (this.startTagFlag != 1))
            this.out = page.getPageContext().popBody();
    }

    public void beforeBody(ZhtmlPage page) throws ZhtmlRuntimeException {
        if ((this.startTagFlag != 0) && (this.startTagFlag != 1)) {
            this.out = page.getPageContext().pushBody();
            BodyTagSupport bts = (BodyTagSupport) this.tagSupport;
            bts.setBodyContent((BodyContent) this.out);
            try {
                bts.doInitBody();
            } catch (JspException e) {
                e.printStackTrace();
                throw new ZhtmlRuntimeException(e.getMessage());
            }
        }
    }

    public JspWriter getOut() {
        return this.out;
    }

    public TagSupport getTagSupport() {
        if (this.tagSupport == null) {
            try {
                this.tagSupport = ((TagSupport) this.Clazz.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this.tagSupport;
    }

    public void setOut(JspWriter out) {
        this.out = out;
    }

    public static class JspTagAttribute {
        public String Name;
        public boolean Required;
    }
}
