package org.itech.labSampleTracker.api.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.controller.BaseController;
import org.itech.labSampleTracker.dto.SampleCollectedDTO;
import org.itech.labSampleTracker.entities.Circuit;
import org.itech.labSampleTracker.entities.Lab;
import org.itech.labSampleTracker.entities.Sample;
import org.itech.labSampleTracker.entities.SampleRejection;
import org.itech.labSampleTracker.entities.SampleRetrieving;
import org.itech.labSampleTracker.entities.SampleStatus;
import org.itech.labSampleTracker.entities.SampleType;
import org.itech.labSampleTracker.entities.Site;
import org.itech.labSampleTracker.entities.TrackingEvent;
import org.itech.labSampleTracker.enums.ESampleStatus;
import org.itech.labSampleTracker.enums.LabType;
import org.itech.labSampleTracker.enums.OrgType;
import org.itech.labSampleTracker.enums.TrackingAction;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tracker")
@Slf4j
public class TrackingController extends BaseController {

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

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

//	@PostMapping("/api_signup")
//	public ResponseEntity<Map<String,Object>> signup(@RequestBody UserDTO userDTO){
//		
//	}

	@GetMapping("/api_login")
	public ResponseEntity<Map<String, Object>> apiLogin() {
		return new ResponseEntity<Map<String, Object>>(this.getCurrentUserInfos(), HttpStatus.OK);
	}

	@PostMapping("/collect_sample_on_site")
	public ResponseEntity<SampleCollectedDTO> collectSampleAtFacility(@RequestBody SampleCollectedDTO collectedSample) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Site requester = siteService.getOne(collectedSample.getSiteId());
		if (ObjectUtils.isEmpty(requester)) {
			requester = new Site();
		}
		Lab destinationLab = labService.getOne(collectedSample.getDestinationLabId());
		if (ObjectUtils.isEmpty(destinationLab)) {
			destinationLab = new Lab();
		}
		try {
			Circuit circuit = circuitService.getOne(collectedSample.getCircuitId());
			if (ObjectUtils.isEmpty(circuit))
				circuit = circuitService.getCircuitByNumber(collectedSample.getCircuitNumber());
			collectedSample.setCircuitId(circuit.getId());
			collectedSample.setStatus(ESampleStatus.ON_TRANSIT.name());

			// create a sampleRetrieving
			SampleRetrieving retrieving = new SampleRetrieving();
			retrieving.setSiteId(collectedSample.getSiteId());
			retrieving.setAppUserId(this.getCurrentUserId());
			retrieving.setSampleRetrieveDate(dateFormat.parse(collectedSample.getCollectionDate()));
			retrieving.setEnteredDate(new Date());
			retrieving = retrievingService.create(retrieving);
			collectedSample.setSampleRetrievingId(retrieving.getId());

			// create a sample
			Sample sample = new Sample();
			SampleType sampleType = sampleTypeService.findByName(collectedSample.getSampleType());
			sample.setSampleTypeId(sampleType.getId());
			sample.setDestinationLabId(
					collectedSample.getDestinationLabId() == 0 ? null : collectedSample.getDestinationLabId());
			sample.setCollectionDate(retrieving.getSampleRetrieveDate());
			sample.setCreatedAt(new Date());
			sample.setPatientIdentifier(collectedSample.getPatientIdentifier());
			sample.setSampleIdentifier(collectedSample.getSampleIdentifier());
			sample.setSampleRetrievingId(retrieving.getId());
			sample.setSampleStatusId(sampleStatusService.findByStatus(ESampleStatus.ON_TRANSIT.name()).getId());
			sample.setRequesterSiteName(requester.getName());
			sample.setDestinationLabName(destinationLab.getLabName());
			sample.setLastupdatedAt(new Date());
			sample.setCollectionStartMileage(collectedSample.getCollectionStartMileage());
			sampleService.create(sample);
			collectedSample.setSampleId(sample.getId());
			collectedSample.setRequesterSiteName(requester.getName());
			collectedSample.setDestinationLabName(destinationLab.getLabName());

			// create a tracking event
			TrackingEvent trackingEvent = new TrackingEvent();
			trackingEvent.setAppUserId(this.getCurrentUserId());
			trackingEvent.setSampleId(sample.getId());
			trackingEvent.setCreatedAt(new Date());
			trackingEvent.setEventType(TrackingAction.COLLECT_SAMPLE_AT_FACILITY.name());
			trackingEvent.setMileage(collectedSample.getCollectionStartMileage());
			trackingEvent.setOrgId(collectedSample.getSiteId());
			trackingEvent.setOrgType(OrgType.SITE.name());
			eventService.create(trackingEvent);

		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/deliver_sample_at_hub")
	public ResponseEntity<SampleCollectedDTO> deliverSampleAtHub(@RequestBody SampleCollectedDTO collectedSample) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setSampleStatusId(sampleStatusService.findByStatus(ESampleStatus.RECEIVED_AT_HUB.name()).getId());
			sample.setDeliverAtHubDate(new Date());
			sample.setHubId(collectedSample.getHubId());
			sample.setLabNumber(collectedSample.getLabNumber());
			sample.setLastupdatedAt(new Date());
			sample.setCollectionEndMileage(collectedSample.getCollectionEndMileage());
			sample = sampleService.create(sample);
			collectedSample.setDeliveredAtHubDate(dateFormat.format(new Date()));
			collectedSample.setStatus(ESampleStatus.RECEIVED_AT_HUB.name());

			// create a tracking event
			TrackingEvent trackingEvent = new TrackingEvent();
			trackingEvent.setAppUserId(this.getCurrentUserId());
			trackingEvent.setSampleId(sample.getId());
			trackingEvent.setCreatedAt(new Date());
			trackingEvent.setEventType(TrackingAction.DELIVER_SAMPLE_AT_HUB.name());
			trackingEvent.setMileage(collectedSample.getCollectionEndMileage());
			trackingEvent.setOrgId(collectedSample.getHubId());
			trackingEvent.setOrgType(OrgType.HUB.name());
			eventService.create(trackingEvent);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/transfer_sample")
	public ResponseEntity<SampleCollectedDTO> transferSample(@RequestBody SampleCollectedDTO collectedSample) {
		try {
			Lab destinationLab = labService.getOne(collectedSample.getDestinationLabId());
			if (ObjectUtils.isEmpty(destinationLab)) {
				destinationLab = new Lab();
			}
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setDestinationLabId(collectedSample.getDestinationLabId());
			sample.setLastupdatedAt(new Date());
			sample.setDestinationLabName(destinationLab.getLabName());
			sample = sampleService.create(sample);
			collectedSample.setDestinationLabName(destinationLab.getLabName());
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/accept_sample_at_hub")
	public ResponseEntity<SampleCollectedDTO> acceptSampleAtHub(@RequestBody SampleCollectedDTO collectedSample) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setLabNumber(collectedSample.getLabNumber());
			sample.setSampleStatusId(sampleStatusService.findByStatus(ESampleStatus.ACCEPTED_AT_HUB.name()).getId());
			sample.setAcceptedAtHubDate(dateFormat.parse(collectedSample.getAcceptedAtHubDate()));
			sample.setHubId(collectedSample.getHubId());
			sample.setLastupdatedAt(new Date());
			sample = sampleService.create(sample);
			collectedSample.setStatus(ESampleStatus.ACCEPTED_AT_HUB.name());
			collectedSample.setAcceptedAtHubDate(dateFormat.format(new Date()));
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/reject_sample")
	public ResponseEntity<SampleCollectedDTO> rejectSample(@RequestBody SampleCollectedDTO collectedSample) {
		try {
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setSampleStatusId(sampleStatusService.findByStatus(ESampleStatus.NON_CONFORM.name()).getId());
			sample.setRejectionDate(new Date());
			sample.setLastupdatedAt(new Date());
			sample = sampleService.create(sample);
			// create SampleRejection
			SampleRejection rejection = new SampleRejection();
			rejection.setCreatedAt(new Date());
			rejection.setSampleRejectionTypeId(collectedSample.getRejectionTypeId());
			rejection.setSampleId(sample.getId());
			rejection.setComment(collectedSample.getRejectionComment());
			rejection = rejectionService.create(rejection);

		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		collectedSample.setStatus(ESampleStatus.NON_CONFORM.name());
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/collect_sample_at_hub")
	public ResponseEntity<SampleCollectedDTO> collectSampleAtHub(@RequestBody SampleCollectedDTO collectedSample) {
		try {
			Sample sample = new Sample();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

			// create a sampleRetrieving
			SampleRetrieving retrieving = new SampleRetrieving();
			retrieving.setSiteId(collectedSample.getSiteId());
			retrieving.setAppUserId(this.getCurrentUserId());
			retrieving.setSampleRetrieveDate(dateFormat.parse(collectedSample.getCollectionDate()));
			retrieving = retrievingService.create(retrieving);
			collectedSample.setSampleRetrievingId(retrieving.getId());

			// get or create a new sample
			if (ObjectUtils.isNotEmpty(collectedSample.getSampleId())) {
				sample = sampleService.getOne(collectedSample.getSampleId());
			}
			if (ObjectUtils.isEmpty(sample)) {
				if (ObjectUtils.isNotEmpty(collectedSample.getSampleIdentifier())) {
					sample = sampleService.findBySampleIdentifier(collectedSample.getSampleIdentifier());
				}
			}
			if (ObjectUtils.isEmpty(sample)) {
				// create a new Sample
				sample = new Sample();
				SampleType sampleType = sampleTypeService.findByName(collectedSample.getSampleType());
				sample.setSampleTypeId(sampleType.getId());
				sample.setHubId(collectedSample.getHubId());
				sample.setCollectionDate(retrieving.getSampleRetrieveDate());
				sample.setCreatedAt(new Date());
				sample.setPatientIdentifier(collectedSample.getPatientIdentifier());
				sample.setSampleIdentifier(collectedSample.getSampleIdentifier());
				sample.setSampleRetrievingId(retrieving.getId());
				sample.setSampleStatusId(sampleStatusService.findByStatus(ESampleStatus.ON_TRANSIT.name()).getId());
				sample.setLabNumber(collectedSample.getLabNumber());
				sampleService.create(sample);
				collectedSample.setSampleId(sample.getId());
			}
			sample.setLastupdatedAt(new Date());
			collectedSample.setStatus(ESampleStatus.ON_TRANSIT.name());
			sample = sampleService.create(sample);

			// create a tracking event
			TrackingEvent trackingEvent = new TrackingEvent();
			trackingEvent.setSampleId(sample.getId());
			trackingEvent.setAppUserId(this.getCurrentUserId());
			trackingEvent.setCreatedAt(new Date());
			trackingEvent.setEventType(TrackingAction.COLLECT_SAMPLE_AT_HUB.name());
			trackingEvent.setMileage(collectedSample.getCollectionStartMileage());
			trackingEvent.setOrgId(collectedSample.getSiteId());
			trackingEvent.setOrgType(OrgType.HUB.name());
			eventService.create(trackingEvent);

		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/deliver_sample_at_lab")
	public ResponseEntity<SampleCollectedDTO> deliverSampleAtLab(@RequestBody SampleCollectedDTO collectedSample) {
		try {
			// get lab
			Lab lab = labService.getOne(collectedSample.getLabId());
			if (ObjectUtils.isEmpty(lab)) {
				return new ResponseEntity<SampleCollectedDTO>(new SampleCollectedDTO(), HttpStatus.OK);
			}
			String status = ESampleStatus.RECEIVED_AT_REFERENCE_LAB.name();
			if (lab.getLabType().equalsIgnoreCase(LabType.PLATEFORM_LAB.getType())) {
				status = ESampleStatus.RECEIVED_AT_REFERENCE_LAB.name();
			} else if (lab.getLabType().equalsIgnoreCase(LabType.TB_LAB.getType())) {
				status = ESampleStatus.RECEIVED_AT_TB_LAB.name();
			} else if (lab.getLabType().equalsIgnoreCase(LabType.DISTRICT_LAB.getType())) {
				status = ESampleStatus.RECEIVED_AT_DISTRICT_LAB.name();
			}
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setSampleStatusId(sampleStatusService.findByStatus(status).getId());
			sample.setDeliverAtLabDate(new Date());
			sample.setLabId(collectedSample.getLabId());
			sample.setLastupdatedAt(new Date());
			sample.setCollectionEndMileage(collectedSample.getCollectionEndMileage());
			sample = sampleService.create(sample);
			collectedSample.setStatus(status);

			// create a tracking event
			TrackingEvent trackingEvent = new TrackingEvent();
			trackingEvent.setSampleId(sample.getId());
			trackingEvent.setAppUserId(this.getCurrentUserId());
			trackingEvent.setCreatedAt(new Date());
			trackingEvent.setEventType(TrackingAction.DELIVER_SAMPLE_AT_LAB.name());
			trackingEvent.setMileage(collectedSample.getCollectionEndMileage());
			trackingEvent.setOrgId(collectedSample.getLabId());
			trackingEvent.setOrgType(OrgType.LAB.name());
			eventService.create(trackingEvent);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/accept_sample_at_lab")
	public ResponseEntity<SampleCollectedDTO> acceptSampleAtLab(@RequestBody SampleCollectedDTO collectedSample) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			// get lab status
			Lab lab = labService.getOne(collectedSample.getLabId());
			if (ObjectUtils.isEmpty(lab)) {
				return new ResponseEntity<SampleCollectedDTO>(new SampleCollectedDTO(), HttpStatus.OK);
			}
			String status = ESampleStatus.ACCEPTED_AT_REFERENCE_LAB.name();
			if (lab.getLabType().equalsIgnoreCase(LabType.PLATEFORM_LAB.getType())) {
				status = ESampleStatus.ACCEPTED_AT_REFERENCE_LAB.name();
			} else if (lab.getLabType().equalsIgnoreCase(LabType.TB_LAB.getType())) {
				status = ESampleStatus.ACCEPTED_AT_TB_LAB.name();
			} else if (lab.getLabType().equalsIgnoreCase(LabType.DISTRICT_LAB.getType())) {
				status = ESampleStatus.ACCEPTED_AT_DISTRICT_LAB.name();
			}
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setSampleStatusId(sampleStatusService.findByStatus(status).getId());
			sample.setAcceptedAtLabDate(new Date());
			sample.setLabNumber(collectedSample.getLabNumber());
			sample.setLabId(collectedSample.getLabId());
			sample.setLastupdatedAt(new Date());
			sample = sampleService.create(sample);
			collectedSample.setAcceptedAtReferenceLabDate(dateFormat.format(new Date()));
			collectedSample.setStatus(status);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/collect_result")
	public ResponseEntity<SampleCollectedDTO> collectResult(@RequestBody SampleCollectedDTO collectedSample) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setSampleStatusId(sampleStatusService.findByStatus(ESampleStatus.RESULT_COLLECTED.name()).getId());
			sample.setResultCollectionDate(new Date());
			sample.setLastupdatedAt(new Date());
			sample.setResultStartMileage(collectedSample.getResultStartMileage());
			sample = sampleService.create(sample);
			collectedSample.setResultCollectionDate(dateFormat.format(new Date()));

			// create a tracking event
			TrackingEvent trackingEvent = new TrackingEvent();
			trackingEvent.setSampleId(sample.getId());
			trackingEvent.setAppUserId(this.getCurrentUserId());
			trackingEvent.setCreatedAt(new Date());
			trackingEvent.setEventType(TrackingAction.COLLECT_RESULT_AT_LAB.name());
			trackingEvent.setMileage(collectedSample.getResultStartMileage());
			trackingEvent.setOrgId(collectedSample.getLabId());
			trackingEvent.setOrgType(OrgType.LAB.name());
			eventService.create(trackingEvent);

		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/deliver_result")
	public ResponseEntity<SampleCollectedDTO> deliverResult(@RequestBody SampleCollectedDTO collectedSample) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setSampleStatusId(sampleStatusService.findByStatus(ESampleStatus.RESULT_ON_SITE.name()).getId());
			sample.setResultEndMileage(collectedSample.getResultEndMileage());
			sample.setResultDeliveryDate(new Date());
			sample.setLastupdatedAt(new Date());
			sample = sampleService.create(sample);

			collectedSample.setResultDeliveryDate(dateFormat.format(new Date()));
			collectedSample.setStatus(ESampleStatus.RESULT_ON_SITE.name());

			// create a tracking event
			TrackingEvent trackingEvent = new TrackingEvent();
			trackingEvent.setSampleId(sample.getId());
			trackingEvent.setAppUserId(this.getCurrentUserId());
			trackingEvent.setCreatedAt(new Date());
			trackingEvent.setEventType(TrackingAction.DELIVER_RESULT_AT_FACILITY.name());
			trackingEvent.setMileage(collectedSample.getResultEndMileage());
			trackingEvent.setOrgId(collectedSample.getLabId());
			trackingEvent.setOrgType(OrgType.SITE.name());
			eventService.create(trackingEvent);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/analysis_done")
	public ResponseEntity<SampleCollectedDTO> analysisDone(@RequestBody SampleCollectedDTO collectedSample) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setSampleStatusId(sampleStatusService.findByStatus(ESampleStatus.ANALYSIS_DONE.name()).getId());
			sample.setAnalysisCompletedDate(dateFormat.parse(collectedSample.getAnalysisCompletedDate()));
			sample.setAnalysisReleasedDate(dateFormat.parse(collectedSample.getAnalysisReleasedDate()));
			sample.setResultReportedDate(new Date());
			sample.setLastupdatedAt(new Date());
			sample = sampleService.create(sample);

			collectedSample.setAnalysisResultReportedDate(dateFormat.format(sample.getResultReportedDate()));
			collectedSample.setStatus(ESampleStatus.ANALYSIS_DONE.name());
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping("/analysis_failed")
	public ResponseEntity<SampleCollectedDTO> analysisFailed(@RequestBody SampleCollectedDTO collectedSample) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			// get the sample
			Sample sample = sampleService.getOne(collectedSample.getSampleId());
			sample.setSampleStatusId(sampleStatusService.findByStatus(ESampleStatus.ANALYSIS_FAILED.name()).getId());
			sample.setAnalysisCompletedDate(dateFormat.parse(collectedSample.getAnalysisCompletedDate()));
			sample.setLastupdatedAt(new Date());
			sample = sampleService.create(sample);

			collectedSample.setStatus(ESampleStatus.ANALYSIS_FAILED.name());
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<>(collectedSample, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(collectedSample, HttpStatus.CREATED);
	}

	@PostMapping(value = "delete_sample/{sampleId}", produces = "application/json")
	public ResponseEntity<Boolean> deleteSample(@PathVariable Integer sampleId) {
		Sample sampleToDelete = sampleService.getOne(sampleId);
		if (ObjectUtils.isNotEmpty(sampleToDelete)) {
			SampleStatus sampleStatus = sampleStatusService.findByStatus(ESampleStatus.ON_TRANSIT.name());
			if (sampleToDelete.getSampleStatusId().equals(sampleStatus.getId())) {
				Boolean result = sampleService.delete(sampleId);
				return new ResponseEntity<Boolean>(result, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = "sample_in_transit", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSampleInTransit() {
		List<Map<String, Object>> sampleList = sampleService.getSampleInTransitForUser(this.getCurrentUserId());
		return new ResponseEntity<List<Map<String, Object>>>(sampleList, HttpStatus.OK);
	}

	@GetMapping(value = "result_for_site/{siteId}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSampleResultForSite(@PathVariable Integer siteId) {
		List<Map<String, Object>> sampleList = sampleService.getSampleResultForUserAndSite(this.getCurrentUserId(),
				siteId);
		return new ResponseEntity<List<Map<String, Object>>>(sampleList, HttpStatus.OK);
	}

	@GetMapping(value = "sample_in_transit/lab/{labId}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSampleInTransitForLab(@PathVariable Integer labId) {
		List<Map<String, Object>> sampleList = new ArrayList<Map<String, Object>>();
		Lab theLab = labService.getOne(labId);
		List<String> plateformeSampleTypes = Arrays.asList("CV", "EID", "BI", "BS", "HPV");
		List<String> districtSampleTypes = Arrays.asList("BI", "BS");
		List<String> catSampleTypes = Arrays.asList("HPV", "TB");
		List<String> hubSampleTypes = Arrays.asList("CV", "EID", "BI", "BS", "HPV", "TB");
		if (ObjectUtils.isNotEmpty(theLab)) {
			if (theLab.getLabType().equalsIgnoreCase(LabType.PLATEFORM_LAB.getType())) {
				sampleList = sampleService.getSampleInTransitForUserAndLab(this.getCurrentUserId(), labId,
						plateformeSampleTypes);
			} else if (theLab.getLabType().equalsIgnoreCase(LabType.HUB_LAB.getType())) {
				sampleList = sampleService.getSampleInTransitForUserAndLab(this.getCurrentUserId(), labId,
						hubSampleTypes);
			} else if (theLab.getLabType().equalsIgnoreCase(LabType.DISTRICT_LAB.getType())) {
				sampleList = sampleService.getSampleInTransitForUserAndLab(this.getCurrentUserId(), labId,
						districtSampleTypes);
			} else if (theLab.getLabType().equalsIgnoreCase(LabType.TB_LAB.getType())) {
				sampleList = sampleService.getSampleInTransitForUserAndLab(this.getCurrentUserId(), labId,
						catSampleTypes);
			} else {
				sampleList = sampleService.getSampleInTransitForUserAndLab(this.getCurrentUserId(), labId);
			}
		}

		return new ResponseEntity<List<Map<String, Object>>>(sampleList, HttpStatus.OK);
	}

	@GetMapping(value = "sample_at_hub/{hubId}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSampleAtHub(@PathVariable Integer hubId) {
		List<Map<String, Object>> sampleList = sampleService.getSampleAtHubForAcceptance(hubId);
		return new ResponseEntity<List<Map<String, Object>>>(sampleList, HttpStatus.OK);
	}

	@GetMapping(value = "sample_at_hub_accepted/{hubId}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSampleAtHubAccepted(@PathVariable Integer hubId) {
		List<Map<String, Object>> sampleList = sampleService.getAcceptedSampleAtHub(hubId);
		return new ResponseEntity<List<Map<String, Object>>>(sampleList, HttpStatus.OK);
	}

	@GetMapping(value = "sample_at_lab/{labId}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSampleAtLab(@PathVariable Integer labId) {
		// get lab status
		Lab lab = labService.getOne(labId);
		if (ObjectUtils.isEmpty(lab)) {
			return new ResponseEntity<List<Map<String, Object>>>(new ArrayList<Map<String, Object>>(), HttpStatus.OK);
		}
		String status = ESampleStatus.RECEIVED_AT_REFERENCE_LAB.name();
		if (lab.getLabType().equalsIgnoreCase(LabType.PLATEFORM_LAB.getType())) {
			status = ESampleStatus.RECEIVED_AT_REFERENCE_LAB.name();
		} else if (lab.getLabType().equalsIgnoreCase(LabType.TB_LAB.getType())) {
			status = ESampleStatus.RECEIVED_AT_TB_LAB.name();
		} else if (lab.getLabType().equalsIgnoreCase(LabType.DISTRICT_LAB.getType())) {
			status = ESampleStatus.RECEIVED_AT_DISTRICT_LAB.name();
		}
		List<Map<String, Object>> sampleList = sampleService.getSampleAtLabForAcceptance(labId, status);
		return new ResponseEntity<List<Map<String, Object>>>(sampleList, HttpStatus.OK);
	}

	@GetMapping(value = "sample_at_lab_accepted/{labId}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getSampleAtLabAccepted(@PathVariable Integer labId) {

		Lab lab = labService.getOne(labId);
		if (ObjectUtils.isEmpty(lab)) {
			return new ResponseEntity<List<Map<String, Object>>>(new ArrayList<Map<String, Object>>(), HttpStatus.OK);
		}
		String status = ESampleStatus.ACCEPTED_AT_REFERENCE_LAB.name();
		if (lab.getLabType().equalsIgnoreCase(LabType.PLATEFORM_LAB.getType())) {
			status = ESampleStatus.ACCEPTED_AT_REFERENCE_LAB.name();
		} else if (lab.getLabType().equalsIgnoreCase(LabType.TB_LAB.getType())) {
			status = ESampleStatus.ACCEPTED_AT_TB_LAB.name();
		} else if (lab.getLabType().equalsIgnoreCase(LabType.DISTRICT_LAB.getType())) {
			status = ESampleStatus.ACCEPTED_AT_DISTRICT_LAB.name();
		}

		List<Map<String, Object>> sampleList = sampleService.getAcceptedSampleAtLab(labId, status);
		return new ResponseEntity<List<Map<String, Object>>>(sampleList, HttpStatus.OK);
	}

	@GetMapping(value = "analysis_done_at_lab/{labId}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getAnalysisDoneAtLab(@PathVariable Integer labId) {
		List<Map<String, Object>> sampleList = sampleService.getAnalysisDoneAtLab(labId);
		return new ResponseEntity<List<Map<String, Object>>>(sampleList, HttpStatus.OK);
	}

	@GetMapping(value = "analysis_rejected_at_lab/{labId}", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getAnalysisRejectedAtLab(@PathVariable Integer labId) {
		List<Map<String, Object>> sampleList = sampleService.getAnalysisFailedAtLab(labId);
		return new ResponseEntity<List<Map<String, Object>>>(sampleList, HttpStatus.OK);
	}

	@GetMapping(value = "sample_count_by_status", produces = "application/json")
	public ResponseEntity<Map<String, Object>> getSampleByStatus() {
		Map<String, Object> sampleCount = sampleService.getSampleCountByStatus();
		return new ResponseEntity<Map<String, Object>>(sampleCount, HttpStatus.OK);
	}

	@GetMapping(value = "sample_status_by_sample_type", produces = "application/json")
	public ResponseEntity<Map<String, Object>> getSampleStatusBySampleType(
			@RequestParam(required = false, name = "region", defaultValue = "0") Integer region,
			@RequestParam(required = false, name = "district", defaultValue = "0") Integer district,
			@RequestParam(required = false, name = "site", defaultValue = "0") Integer site,
			@RequestParam(required = false, name = "circuit", defaultValue = "0") Integer circuit,
			@RequestParam(required = false, name = "start", defaultValue = "") String startDateString,
			@RequestParam(required = false, name = "end", defaultValue = "") String endDateString) {
		Map<String, Object> response = new HashMap<String, Object>();

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

		Map<String, Map<String, Integer>> sampleCount = sampleService.getSampleStatusBySampleType(region,district,site,startDate,endDate);
		Set<String> categories = sampleCount.keySet();
		List<Integer> collected = new ArrayList<Integer>();
		List<Integer> delivered = new ArrayList<Integer>();
		List<Integer> nonConform = new ArrayList<Integer>();
		List<Integer> failed = new ArrayList<Integer>();

		categories.forEach(el -> {
			sampleCount.forEach((k, v) -> {
				if (el.equalsIgnoreCase(k)) {
					collected.add(v.get("COLLECTES"));
					delivered.add(v.get("TRANSMIS"));
					nonConform.add(v.get("NON CONFORME"));
					failed.add(v.get("ECHEC"));
				}
			});
		});
		response.put("categories", categories);
		response.put("collected", collected);
		response.put("delivered", delivered);
		response.put("nonConform", nonConform);
		response.put("failed", failed);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

}
