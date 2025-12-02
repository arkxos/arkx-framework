package io.arkx.framework.boot.spring;

import java.util.concurrent.atomic.LongAdder;

import org.springframework.context.annotation.Conditional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import io.arkx.framework.WebCurrent;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author darkness
 * @date 2018-08-26 14:59:28
 * @version 1.0
 * @since 4.0
 */
@Slf4j
@Conditional(RawArkFrameworkCondition.class)
public class ArkSessionInterceptor implements HandlerInterceptor {

    LongAdder preCounter = new LongAdder();
    LongAdder postCounter = new LongAdder();
    LongAdder finishCounter = new LongAdder();

    public ArkSessionInterceptor() {
        System.out.println("init ArkSessionInterceptor：" + this.toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.
     * http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        // option预检查，直接通过请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        preCounter.increment();

        WebCurrent.prepare(request, response);

        Session session = SessionFactory.openSessionInThread();
        session.beginTransaction();

        log.debug("preHandle:[" + preCounter.longValue() + "(" + session.getConnection().connID + ")] "
                + request.getRequestURI());

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.
     * http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
        postCounter.increment();
        Session session = SessionFactory.currentSession();
        if (session != null) {
            log.debug("postHandle:[" + postCounter.longValue() + "(" + session.getConnection().connID + ")] "
                    + request.getRequestURI());

            session.commit();
            SessionFactory.clearCurrentSession();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.
     * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * java.lang.Object, java.lang.Exception)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse arg1, Object arg2, Exception exception)
            throws Exception {
        finishCounter.increment();
        // Session session = SessionFactory.currentSession();
        // if(session != null) {
        // session.commit();
        // SessionFactory.clearCurrentSession();
        // }
        Session session = SessionFactory.currentSession();
        if (session != null && session.getConnection() != null) {
            log.debug("afterCompletion:[" + finishCounter.longValue() + "(" + session.getConnection().connID + ")] "
                    + request.getRequestURI());
        } else {
            log.debug("afterCompletion:[" + finishCounter.longValue() + "] " + request.getRequestURI());

        }
        if (exception != null) {
            if (session != null) {
                session.rollback();
                SessionFactory.clearCurrentSession();
            }
        }
    }
}
