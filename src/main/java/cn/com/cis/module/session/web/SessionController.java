package cn.com.cis.module.session.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SessionController {
	
	@RequestMapping(value="/session")
	public ModelAndView drgsGroupByHisId(){
		ModelAndView modelAndView = new ModelAndView("session");
		return modelAndView;
	}

	@RequestMapping("/setsession")
	@ResponseBody
	public void setSession(HttpSession session, String name) {
		session.setAttribute("name", name);
	}

	@RequestMapping(value = "/getsession", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String getSession(HttpSession session, HttpServletRequest req) {
		return session.getAttribute("name").toString();
	}
}
