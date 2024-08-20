package org.itech.labSampleTracker.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.service.AppUserService;
import org.itech.labSampleTracker.service.DistrictService;
import org.itech.labSampleTracker.service.LabService;
import org.itech.labSampleTracker.service.RegionService;
import org.itech.labSampleTracker.service.ReportService;
import org.itech.labSampleTracker.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = { "/report" })
@Slf4j
public class ReportController {
	@Autowired
	private AppUserService userService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private RegionService regionService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private LabService labService;
	
	@Autowired
	private ReportService reportService;
	
	private String riderReportName = "rider_report.jasper";

	@GetMapping(value = "")
	public String reportIndex(Model model) {

		model.addAttribute("regions", regionService.getRegionIdAndName());
		model.addAttribute("districts", districtService.getDistrictIdAndNames());
		model.addAttribute("sites", siteService.getSiteIdAndNames());
		model.addAttribute("labs", labService.getLabIdAndNames());
		model.addAttribute("riders", userService.getRiderIdAndName());
		return "report/index";
	}

	@GetMapping(value = "/print", produces = { MediaType.APPLICATION_PDF_VALUE })
	public ResponseEntity<Resource> printReport(
			@RequestParam(required = false, name = "region", defaultValue = "0") Integer region,
			@RequestParam(required = false, name = "district", defaultValue = "0") Integer district,
			@RequestParam(required = false, name = "site", defaultValue = "0") Integer site,
			@RequestParam(required = false, name = "lab", defaultValue = "0") Integer lab,
			@RequestParam(required = false, name = "rider", defaultValue = "0") Integer rider,
			@RequestParam(required = false, name = "start", defaultValue = "") String startDateString,
			@RequestParam(required = false, name = "end", defaultValue = "") String endDateString) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = null;
		Date endDate = null;
		try {
			if (ObjectUtils.isEmpty(startDateString)) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2000);
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
			log.warn(e.getMessage());
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
		if (lab == 0) {
			lab = null;
		}
		if (rider == 0) {
			rider = null;
		}
		byte[] reportContent = reportService.getReportItems(region, district, site, lab, rider, startDate, endDate, riderReportName);

		ByteArrayResource resource = new ByteArrayResource(reportContent);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
				.contentLength(resource.contentLength())
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.inline().filename("report.pdf").build().toString())
				.body(resource);
	}

}
