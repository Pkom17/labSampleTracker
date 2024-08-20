/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 */
package org.itech.labSampleTracker.controller;

import org.itech.labSampleTracker.entities.District;
import org.itech.labSampleTracker.service.DistrictService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <h2>DistrictController</h2>
 */
@Controller
@RequestMapping("/district")
public class DistrictController {

	@Autowired
	private DistrictService districtService;

	@PostMapping(value = "")
	public String createDistrict(@Valid District model) {

		District data = districtService.create(model);
		if (data != null) {
			return "";
		} else {
			return "";
		}
	}

	@GetMapping(value = "")
	public String getAllDistrict(Model model) {
		List<District> districtList = districtService.getAll();

		model.addAttribute("Districts", districtList);

		return "";
	}

	@GetMapping(value = "/{id}")
	public String getOneDistrict(@PathVariable("id") Integer id, Model model) {

		District e = districtService.getOne(id);
		if (e == null) {
			return "";
		}
		model.addAttribute("district", e);
		return "";
	}

	@GetMapping(value = "/names", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getRegionNames() {
		List<Map<String, Object>> districtList = districtService.getDistrictIdAndNames();
		return new ResponseEntity<List<Map<String, Object>>>(districtList, HttpStatus.OK);
	}
	
	@GetMapping(value = "/names/{region}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getDistrictNames(@PathVariable("region") Integer regionId) {
		List<Map<String, Object>> districtList = districtService.getDistrictIdAndNamesByRegion(regionId);
		return new ResponseEntity<List<Map<String, Object>>>(districtList, HttpStatus.OK);
	}
	
	@GetMapping(value = "/names_by_regions", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getDistrictNamesAll(@RequestParam List<Integer> regions) {
		List<Map<String, Object>> districtList = districtService.getDistrictIdAndNamesByRegions(regions);
		return new ResponseEntity<List<Map<String, Object>>>(districtList, HttpStatus.OK);
	}

}
