package org.itech.labSampleTracker.api.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.itech.labSampleTracker.controller.BaseController;
import org.itech.labSampleTracker.dto.SampleCollectedDTO;
import org.itech.labSampleTracker.entities.Circuit;
import org.itech.labSampleTracker.entities.Sample;
import org.itech.labSampleTracker.entities.SampleAtLab;
import org.itech.labSampleTracker.entities.SampleRejection;
import org.itech.labSampleTracker.entities.SampleRetrieving;
import org.itech.labSampleTracker.entities.SampleType;
import org.itech.labSampleTracker.entities.TrackingEvent;
import org.itech.labSampleTracker.enums.OrgType;
import org.itech.labSampleTracker.enums.SampleStatus;
import org.itech.labSampleTracker.enums.TrackingAction;
import org.itech.labSampleTracker.exception.OperationFailedException;
import org.itech.labSampleTracker.service.AppUserService;
import org.itech.labSampleTracker.service.CircuitService;
import org.itech.labSampleTracker.service.DistrictService;
import org.itech.labSampleTracker.service.LabService;
import org.itech.labSampleTracker.service.RegionService;
import org.itech.labSampleTracker.service.RideService;
import org.itech.labSampleTracker.service.SampleAtLabService;
import org.itech.labSampleTracker.service.SampleRejectionService;
import org.itech.labSampleTracker.service.SampleRetrievingService;
import org.itech.labSampleTracker.service.SampleService;
import org.itech.labSampleTracker.service.SampleStatusService;
import org.itech.labSampleTracker.service.SampleTypeService;
import org.itech.labSampleTracker.service.SiteService;
import org.itech.labSampleTracker.service.TrackingEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tracker/display_list")
@Slf4j
public class DisplayListController extends BaseController {

	@Autowired
	RideService rideService;

	@Autowired
	CircuitService circuitService;

	@Autowired
	SampleService sampleService;

	@Autowired
	SampleAtLabService sampleAtLabService;
	
	@Autowired
	SampleRejectionService rejectionService;

	@Autowired
	SampleRetrievingService retrievingService;

	@Autowired
	TrackingEventService eventService;

	@Autowired
	private AppUserService appuserService;

	@Autowired
	private RegionService regionService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private SampleTypeService sampleTypeService;

	@Autowired
	private SampleStatusService sampleStatusService;

	@Autowired
	private LabService labService;
		
}
