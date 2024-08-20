/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 */
package org.itech.labSampleTracker.controller;

import org.itech.labSampleTracker.entities.Region;
import org.itech.labSampleTracker.service.RegionService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;



/**
* <h2>RegionController</h2>
*/
@Controller
@RequestMapping("/region")
public class RegionController {


	@Autowired
	private RegionService regionService;

	@PostMapping(value = "")
	public String createRegion(@Valid Region model) {

   		 Region data = regionService.create(model);
    		if (data != null) {
    			return "";
  			  } else {
    			return "";
   			 }
    }

    @GetMapping(value = "")
    public String getAllRegion(Model model) {
        List<Region> regionList = regionService.getAll();

		model.addAttribute("Regions",regionList);

        return "";
    }
    
    @GetMapping(value = "names", produces = "application/json")
    public ResponseEntity<List<Map<String,Object>>> getRegionNames() {
        List<Map<String,Object>> regionList = regionService.getRegionIdAndName();
        return new ResponseEntity<List<Map<String,Object>>>(regionList, HttpStatus.OK);
    
    }

        @GetMapping(value = "/{id}")
    public String getOneRegion(@PathVariable("id") Integer id, Model model) {

            Region e = regionService.getOne(id);
            if (e == null) {
            	return "";
            }
			model.addAttribute("region",e);
            return "";
    }



}
