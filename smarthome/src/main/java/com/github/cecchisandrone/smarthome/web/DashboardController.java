package com.github.cecchisandrone.smarthome.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.github.cecchisandrone.smarthome.service.raspsonar.RaspsonarService;
import com.github.cecchisandrone.smarthome.service.raspsonar.RaspsonarServiceException;

/**
 * Handles requests for the application home page.
 */
@Controller
public class DashboardController {

	@Autowired
	private RaspsonarService raspsonarService;

	@RequestMapping(value = {"/dashboard", "/"}, method = RequestMethod.GET)
	public ModelAndView home() {

		ModelAndView modelAndView = new ModelAndView(ViewNames.DASHBOARD);
		try {
			modelAndView.addObject("waterLevel", raspsonarService.getDistance(false));
			modelAndView.addObject("relayStatus", raspsonarService.isRelayStatus());
			modelAndView.addObject("distanceChartUrl", raspsonarService.getDistanceChartUrl());
		} catch (RaspsonarServiceException e) {
			modelAndView.addObject("errorMessage", e.toString());
		}
		return modelAndView;
	}

	@RequestMapping(value = {"/dashboard/resetAverageDistance"}, method = RequestMethod.GET)
	public ModelAndView resetAverageDistance() {

		ModelAndView modelAndView = new ModelAndView(ViewNames.DASHBOARD);
		try {
			modelAndView.addObject("waterLevel", raspsonarService.getDistance(true));
		} catch (RaspsonarServiceException e) {
			modelAndView.addObject("errorMessage", e.toString());
		}
		return modelAndView;
	}

	@RequestMapping(value = {"/dashboard/waterPump/{status}"}, method = RequestMethod.GET)
	public ModelAndView toggleRelay(@PathVariable boolean status, Model model) {

		try {
			raspsonarService.toggleRelay(status);
		} catch (RaspsonarServiceException e) {
			model.addAttribute("errorMessage", e.toString());
		}
		return home();
	}

	@RequestMapping("/error")
	public String error(HttpServletRequest request, Model model) {

		model.addAttribute("errorCode", request.getAttribute("javax.servlet.error.status_code"));
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		String errorMessage = null;
		if (throwable != null) {
			errorMessage = throwable.toString();
		}
		model.addAttribute("errorMessage", errorMessage);
		return ViewNames.ERROR;
	}
}
