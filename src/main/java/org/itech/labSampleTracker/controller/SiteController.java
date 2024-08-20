/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 */
package org.itech.labSampleTracker.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.entities.Site;
import org.itech.labSampleTracker.exception.ResourceNotFoundException;
import org.itech.labSampleTracker.service.DistrictService;
import org.itech.labSampleTracker.service.SiteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/site")
public class SiteController extends BaseController {

	@Autowired
	private SiteService siteService;

	@Autowired
	private DistrictService districtService;


	@GetMapping(value = "/delete/{id}")
	public String deleteSite(@PathVariable("id") int id) {
		siteService.delete(id);
		return "redirect:/site";
	}

	@PostMapping(value = "/new")
	public String createSite(Model model, @Valid Site site) {
		try {
			Site data = siteService.create(site);
			if (data != null) {
				model.addAttribute("message_success", "Ajout effectué avec succès");
			} else {
				model.addAttribute("message_error", "Impossible d'ajouter le site");
			}
		} catch (Exception e) {
			model.addAttribute("message_error", e.getMessage());
		}
		model.addAttribute("site", new Site());
		return "site/new";
	}

	@GetMapping(value = "/new")
	public String newAppUser(Model model) {
		model.addAttribute("districts", districtService.getDistrictIdAndNames());
		model.addAttribute("site", new Site());
		return "site/new";
	}

	@GetMapping(value = "")
	public String getAllSite(Model model) {
		List<Site> siteList = siteService.getAll();
		model.addAttribute("sites", siteList);
		return "site/index";
	}

	@GetMapping(value = "/update/{id}")
	public String getOneSite(@PathVariable("id") Integer id, Model model) {
		Site site = new Site();
		try {
			site = siteService.getOne(id);
			if (ObjectUtils.isEmpty(site)) {
				throw new ResourceNotFoundException("Impossible de retrouver les données de ce site ");
			}
		} catch (Exception ex) {
			model.addAttribute("message_error", ex.getMessage());
		}
		model.addAttribute("districts", districtService.getDistrictIdAndNames());
		model.addAttribute("site", site);
		return "site/edit";
	}

	@PostMapping(value = "/update/{id}")
	public String updateSite(@PathVariable("id") Integer id, @Valid Site site, Model model) {
		Site siteToUpdate;
		model.addAttribute("districts", districtService.getDistrictIdAndNames());
		try {
			siteToUpdate = siteService.getOne(id);
			if (ObjectUtils.isEmpty(site)) {
				model.addAttribute("message_error", "Impossible de retrouver les données de ce site");
				return "site/edit";
			}

			BeanUtils.copyProperties(site, siteToUpdate, "id");
			siteService.create(siteToUpdate);
			model.addAttribute("message_success", "Modification effectuée avec succès");
		} catch (Exception ex) {
			model.addAttribute("message_error", ex.getMessage());
			model.addAttribute("site", site);
			return "site/edit";
		}
		
		model.addAttribute("site", siteToUpdate);
		return "site/edit";
	}

	@GetMapping(value = "names", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSiteNames() {
		List<Map<String, Object>> siteList = siteService.getSiteIdAndNames();
		return new ResponseEntity<List<Map<String, Object>>>(siteList, HttpStatus.OK);
	}
	
	@GetMapping(value = "names_circuits", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSiteNamesCircuits() {
		List<Map<String, Object>> siteList = siteService.getSiteIdAndNamesAndCicuit();
		return new ResponseEntity<List<Map<String, Object>>>(siteList, HttpStatus.OK);
	}

	@GetMapping(value = "names/{district}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSiteNames(@PathVariable("district") Integer districtId) {
		List<Map<String, Object>> siteList = siteService.getSiteIdAndNamesByDistrict(districtId);
		return new ResponseEntity<List<Map<String, Object>>>(siteList, HttpStatus.OK);
	}

	@GetMapping(value = "names_by_districts", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getDistrictNamesAll(@RequestParam List<Integer> districts) {
		List<Map<String, Object>> siteList = siteService.getSiteIdAndNamesByDistricts(districts);
		return new ResponseEntity<List<Map<String, Object>>>(siteList, HttpStatus.OK);
	}
	
	@GetMapping(value = "names/by_user", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSiteNamesByUser() {
		List<Map<String, Object>> siteList = siteService.getSiteIdAndCodeAndNamesByUser(this.getCurrentUserId());
		return new ResponseEntity<List<Map<String, Object>>>(siteList, HttpStatus.OK);
	}

}
