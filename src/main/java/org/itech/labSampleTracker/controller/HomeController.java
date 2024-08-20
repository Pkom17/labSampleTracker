package org.itech.labSampleTracker.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.service.AppUserService;
import org.itech.labSampleTracker.service.DistrictService;
import org.itech.labSampleTracker.service.LoginService;
import org.itech.labSampleTracker.service.RegionService;
import org.itech.labSampleTracker.service.SampleService;
import org.itech.labSampleTracker.service.SampleTypeService;
import org.itech.labSampleTracker.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = { "/", "/home" })
@Slf4j
public class HomeController {
	@Autowired
	private AppUserService userService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private RegionService regionService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private SampleService sampleService;
	
	@Autowired
	private SampleTypeService sampleTypeService;

	@GetMapping(value = "/dashboard")
	public String index() {
		return "redirect:/dashboard";
	}

	@GetMapping(value = "")
	public String analysisIndex(Model model,
			@RequestParam(required = false, name = "start", defaultValue = "") String startDateString,
			@RequestParam(required = false, name = "end", defaultValue = "") String endDateString,
			@RequestParam(required = false, name = "region", defaultValue = "0") Integer region,
			@RequestParam(required = false, name = "district", defaultValue = "0") Integer district,
			@RequestParam(required = false, name = "site", defaultValue = "0") Integer site,
			@RequestParam(required = false, name = "circuit", defaultValue = "0") Integer circuit,
			@RequestParam(required = false, name = "sample_type", defaultValue = "0") Integer sampleType) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = null;
		Date endDate = null;
			try {
				if (ObjectUtils.isEmpty(startDateString)) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.YEAR, 2020);
					calendar.set(Calendar.MONTH, Calendar.JANUARY);
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					startDate = calendar.getTime();
				} else {
					startDate = sdf.parse(startDateString);
				}
				if (ObjectUtils.isEmpty(endDateString)) {
					endDate = new Date();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					endDate = calendar.getTime();
				} else {
					endDate = sdf.parse(endDateString);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DAY_OF_MONTH, 1);
					endDate = calendar.getTime();
				}

			} catch (Exception e) {
				startDate = null;
				startDateString = null;
				endDate = null;
				endDateString = null;
			}
			
			if (region == 0) {
				region = null;
			}
			if (district == 0) {
				district = null;
			}
			if (site == 0) {
				site = null;
			}
			if (circuit == 0) {
				circuit = null;
			}

		Map<String, Object> sampleCount = sampleService.getSampleCountByStatus(region,district,site,startDate,endDate);
		
		List<Map<String, Object>> sites = siteService.getSiteIdAndNames();
		List<Map<String, Object>> regions = regionService.getRegionIdAndName();
		List<Map<String, Object>> districts = districtService.getDistrictIdAndNames();
		List<Map<String, Object>> sampleTypes = sampleTypeService.getSampleTypeIdAndName();
		if(ObjectUtils.isNotEmpty(region)) {
			districts = districtService.getDistrictIdAndNamesByRegion(region);
		}
		if(ObjectUtils.isNotEmpty(district)) {
			sites = siteService.getSiteIdAndNamesByDistrict(district);
		}

		model.addAttribute("sampleCount", sampleCount);
		model.addAttribute("regions", regions);
		model.addAttribute("districts", districts);
		model.addAttribute("sampleTypes", sampleTypes);
		model.addAttribute("sites", sites);
		model.addAttribute("selectedSite", site);
		model.addAttribute("selectedRegion", region);
		model.addAttribute("selectedDistrict", district);
		model.addAttribute("selectedStartDate", startDateString);
		model.addAttribute("selectedEndDate", endDateString);
		return "home/index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/403")
	public String error403() {
		return "error";
	}

}
