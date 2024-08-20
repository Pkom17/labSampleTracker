package org.itech.labSampleTracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = { "/setting" })
public class SettingController {

	@GetMapping(value = "")
	public String settingIndex(Model model) {
		return "setting/index";
	}

}
