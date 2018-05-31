package com.lhk.cms.listener;

import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("===================================这是一个测试");
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		WebApplicationContext application = (WebApplicationContext) se.getSession().getServletContext()
				.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		System.out.println("===================================这是另一个测试");
	}

}
