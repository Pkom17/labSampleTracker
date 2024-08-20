/* 
 * Created on 2022-03-29 ( Date ISO 2022-03-29 - Time 08:26:24 )
 */
package org.itech.labSampleTracker.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.exception.OperationFailedException;
import org.itech.labSampleTracker.service.DashboardService;
import org.itech.labSampleTracker.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	@GetMapping(value = "/sample_trend")
	public ResponseEntity<Map<String, Object>> getPreventiveMaintenance(
			@RequestParam(required = false, name = "region", defaultValue = "0") Integer region,
			@RequestParam(required = false, name = "district", defaultValue = "0") Integer district,
			@RequestParam(required = false, name = "site", defaultValue = "0") Integer site,
			@RequestParam(required = false, name = "circuit", defaultValue = "0") Integer circuit,
			@RequestParam(required = false, name = "start", defaultValue = "") String startDateString,
			@RequestParam(required = false, name = "end", defaultValue = "") String endDateString) {
		Map<String, Object> res = new HashMap<String, Object>();
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

			List<Object> categories = new ArrayList<Object>();
			List<Object> prev = new ArrayList<Object>();
			List<Object> done = new ArrayList<Object>();

			res.put("categories", categories);
			res.put("prev", prev);
			res.put("done", done);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

}
