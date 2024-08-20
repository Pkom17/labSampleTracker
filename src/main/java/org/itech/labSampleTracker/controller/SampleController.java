/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 */
package org.itech.labSampleTracker.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.dto.SampleUpdateDTO;
import org.itech.labSampleTracker.entities.Sample;
import org.itech.labSampleTracker.entities.SampleRejection;
import org.itech.labSampleTracker.entities.SampleStatus;
import org.itech.labSampleTracker.enums.ESampleStatus;
import org.itech.labSampleTracker.exception.ResourceNotFoundException;
import org.itech.labSampleTracker.service.DistrictService;
import org.itech.labSampleTracker.service.LabService;
import org.itech.labSampleTracker.service.RegionService;
import org.itech.labSampleTracker.service.SampleRejectionService;
import org.itech.labSampleTracker.service.SampleRejectionTypeService;
import org.itech.labSampleTracker.service.SampleService;
import org.itech.labSampleTracker.service.SampleStatusService;
import org.itech.labSampleTracker.service.SampleTypeService;
import org.itech.labSampleTracker.service.SiteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>SampleController</h2>
 */
@Controller
@RequestMapping("/sample")
@Slf4j
public class SampleController {

	@Autowired
	private SampleService sampleService;

	@Autowired
	private SampleTypeService sampleTypeService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private LabService labService;

	@Autowired
	private RegionService regionService;

	@Autowired
	private DistrictService districtService;

	@Autowired
	private SampleStatusService sampleStatusService;

	@Autowired
	private SampleRejectionService sampleRejectionService;

	@Autowired
	private SampleRejectionTypeService sampleRejectionTypeService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@PostMapping(value = "")
	public String createSample(@Valid Sample model) {

		Sample data = sampleService.create(model);
		if (data != null) {
			return "";
		} else {
			return "";
		}
	}

	@GetMapping(value = "")
	public String getAllSample(Model model, @RequestParam(defaultValue = "collection_date,desc") String[] sort,
			@RequestParam(required = false, name = "region", defaultValue = "0") Integer region,
			@RequestParam(required = false, name = "district", defaultValue = "0") Integer district,
			@RequestParam(required = false, name = "site", defaultValue = "0") Integer site,
			@RequestParam(required = false, name = "circuit", defaultValue = "0") Integer circuit,
			@RequestParam(required = false, name = "status", defaultValue = "0") Integer status,
			@RequestParam(required = false, name = "start", defaultValue = "") String startDateString,
			@RequestParam(required = false, name = "end", defaultValue = "") String endDateString,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
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
		if (circuit == 0) {
			circuit = null;
		}
		if (status == 0) {
			status = null;
		}

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(this.getSortOrder(sort)));
		Page<Map<String, Object>> sampleListPageable = sampleService.getSampleDetails(pageable, region, district, site,
				startDate, endDate, status);

		List<Map<String, Object>> sites = siteService.getSiteIdAndNames();
		List<Map<String, Object>> regions = regionService.getRegionIdAndName();
		List<Map<String, Object>> districts = districtService.getDistrictIdAndNames();
		List<SampleStatus> statusList = sampleStatusService.getAll();
		if (ObjectUtils.isNotEmpty(region)) {
			districts = districtService.getDistrictIdAndNamesByRegion(region);
		}
		if (ObjectUtils.isNotEmpty(district)) {
			sites = siteService.getSiteIdAndNamesByDistrict(district);
		}

		List<Map<String, Object>> sampleList = sampleListPageable.getContent();
		model.addAttribute("totalElements", sampleListPageable.getTotalElements());
		model.addAttribute("totalPages", sampleListPageable.getTotalPages());
		model.addAttribute("size", sampleListPageable.getSize());
		model.addAttribute("currentPage", sampleListPageable.getNumber() + 1);
		model.addAttribute("numberOfElements", sampleListPageable.getNumberOfElements());
		model.addAttribute("first", sampleListPageable.isFirst());
		model.addAttribute("last", sampleListPageable.isLast());
		model.addAttribute("empty", sampleListPageable.isEmpty());
		model.addAttribute("selectedSite", site);
		model.addAttribute("selectedRegion", region);
		model.addAttribute("selectedDistrict", district);
		model.addAttribute("selectedStartDate", startDateString);
		model.addAttribute("selectedEndDate", endDateString);
		model.addAttribute("selectedStatus", status);
		model.addAttribute("samples", sampleList);

		model.addAttribute("regions", regions);
		model.addAttribute("districts", districts);
		model.addAttribute("sites", sites);
		model.addAttribute("statusList", statusList);

		return "sample/index";
	}

	@PostMapping(value = "/delete/{id}")
	public String deleteSample(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
			if (ObjectUtils.isNotEmpty(id)) {
				if (sampleService.removeById(id)) {
					redirectAttributes.addFlashAttribute("message", "Echantillon supprimé avec succès");
					return "redirect:/sample";
				}
			}
		}
		return "redirect:/sample";
	}

	@GetMapping(value = "/{id}")
	public String getOneSample(@PathVariable("id") Integer id, Model model) {

		Sample e = sampleService.getOne(id);
		if (e == null) {
			return "";
		}
		model.addAttribute("sample", e);
		return "";
	}

	@GetMapping(value = "/update/{id}")
	public String updateSample(@PathVariable("id") Integer id, Model model) {
		SampleUpdateDTO sampleToUpdate = new SampleUpdateDTO();
		Sample sample = new Sample();
		try {
			sample = sampleService.getOne(id);
			if (ObjectUtils.isEmpty(sample)) {
				throw new ResourceNotFoundException("Impossible de retrouver les données de cet échantillon ");
			}

			BeanUtils.copyProperties(sample, sampleToUpdate);
			SampleRejection rejection = sampleRejectionService.getOneBySampleId(id);
			if (ObjectUtils.isNotEmpty(rejection)) {
				sampleToUpdate.setRejectionTypeId(rejection.getId());
				sampleToUpdate.setRejectionComment(rejection.getComment());
			}
			sampleToUpdate.setRequesterSiteId(sample.getSampleRetrieving().getSiteId());

		} catch (Exception ex) {
			model.addAttribute("message_error", ex.getMessage());
		}

		model.addAttribute("sample", sampleToUpdate);
		model.addAttribute("sites", siteService.getSiteIdAndNames());
		model.addAttribute("sampleRejectionTypes", sampleRejectionTypeService.getAll());
		model.addAttribute("labs", labService.getLabIdAndNames());
		model.addAttribute("sampleStatus", sampleStatusService.getAll());
		model.addAttribute("sampleTypes", sampleTypeService.getAll());
		return "sample/edit";
	}

	@PostMapping(value = "/update/{id}")
	public String updateSample(@PathVariable("id") Integer id, @ModelAttribute SampleUpdateDTO sampleToUpdate,
			Model model) {
		Sample updatedSample = new Sample();
		Sample oldSample = new Sample();
		try {
			oldSample = sampleService.getOne(id);
			if (ObjectUtils.isEmpty(oldSample)) {
				throw new ResourceNotFoundException("Impossible de retrouver les données de cet échantillon ");
			}

			BeanUtils.copyProperties(sampleToUpdate, updatedSample);

			if (oldSample.getSampleStatusId()
					.equals(sampleStatusService.findByStatus(ESampleStatus.NON_CONFORM.name()).getId())
					&& !sampleToUpdate.getSampleStatusId()
							.equals(sampleStatusService.findByStatus(ESampleStatus.NON_CONFORM.name()).getId())) {
				// remove rejectedSample
				sampleRejectionService.removeBySampleId(id);

			} else if (!oldSample.getSampleStatusId()
					.equals(sampleStatusService.findByStatus(ESampleStatus.NON_CONFORM.name()).getId())
					&& sampleToUpdate.getSampleStatusId()
							.equals(sampleStatusService.findByStatus(ESampleStatus.NON_CONFORM.name()).getId())) {
				// create SampleRejection
				SampleRejection rejection = new SampleRejection();
				rejection.setCreatedAt(new Date());
				rejection.setSampleRejectionTypeId(sampleToUpdate.getRejectionTypeId());
				rejection.setSampleId(sampleToUpdate.getId());
				rejection.setComment(sampleToUpdate.getRejectionComment());
				rejection = sampleRejectionService.create(rejection);
				updatedSample.setRejectionDate(new Date());
			}

			updatedSample.setSampleRetrievingId(oldSample.getId());
			updatedSample.setLastupdatedAt(new Date());
			updatedSample = sampleService.create(updatedSample);

		} catch (Exception ex) {
			model.addAttribute("message_error", ex.getMessage());
		}

		model.addAttribute("sample", sampleToUpdate);
		model.addAttribute("sites", siteService.getSiteIdAndNames());
		model.addAttribute("sampleRejectionTypes", sampleRejectionTypeService.getAll());
		model.addAttribute("labs", labService.getLabIdAndNames());
		model.addAttribute("sampleStatus", sampleStatusService.getAll());
		model.addAttribute("sampleTypes", sampleTypeService.getAll());
		return "sample/edit";
	}

	protected List<Order> getSortOrder(String[] sorts) {
		List<Order> orders = new ArrayList<>();
		if (sorts[0].contains(",")) {
			for (String sortOrder : sorts) {
				String[] sort = sortOrder.split(",");
				orders.add(new Order(getSortDirection(sort[1]), sort[0]));
			}
		} else {
			orders.add(new Order(getSortDirection(sorts[1]), sorts[0]));
		}

		return orders;
	}

	private Sort.Direction getSortDirection(String direction) {
		if (direction.equals("asc")) {
			return Sort.Direction.ASC;
		} else if (direction.equals("desc")) {
			return Sort.Direction.DESC;
		}
		return Sort.Direction.ASC;
	}

	@GetMapping(value = "/csv")
	public ResponseEntity<Resource> getSampleInCSV(Model model,
			@RequestParam(required = false, name = "region", defaultValue = "0") Integer region,
			@RequestParam(required = false, name = "district", defaultValue = "0") Integer district,
			@RequestParam(required = false, name = "site", defaultValue = "0") Integer site,
			@RequestParam(required = false, name = "status", defaultValue = "0") Integer status,
			@RequestParam(required = false, name = "start", defaultValue = "") String startDateString,
			@RequestParam(required = false, name = "end", defaultValue = "") String endDateString) {
		String filename = "sample_export.csv";
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
		if (status == 0) {
			status = null;
		}

		List<Map<String, String>> sampleRecords = sampleService.getAll(region, district, site, startDate, endDate,
				status);

		Object[] header = { "REGION", "DISTRICT", "SITE DE COLLECTE", "CONVOYEUR", "TYPE ECHANTILLON",
				"IDENTIFIANT PATIENT", "DATE ET HEURE DE COLLECTE", "DESTINATION", "STATUS", "RAISON DU REJET",
				"LABO D'ANALYSE","DATE ET HEURE DE RECEPTION AU LABO", "NUMERO LABO", "DISTANCE PARCOURUE", 
				"DATE ANALYSE", "DATE DISPONIBILITE RESULTAT", "DATE DE COLLECTE RESULTAT",
				"DATE DE RECEPTION RESULTAT", "TAT 1", "TAT 2", "TAT 3" };

		InputStreamResource file = new InputStreamResource(writeCSVData(header, sampleRecords));

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(file);

	}

	private ByteArrayInputStream writeCSVData(Object[] header, List<Map<String, String>> data) {
		final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);
			csvPrinter.printRecord(header);

			data.forEach(el -> {
				Object[] d = { 
					el.get("region"),el.get("district"),el.get("site"),el.get("rider"),el.get("sampleType"),el.get("patientIdentifier"),el.get("collectionDate")
					,el.get("destinationLab"),el.get("status"),el.get("rejectionComment"),el.get("lab"),el.get("receptionDate"),el.get("labNumber"),el.get("distance")
					,el.get("analysisCompletedDate"),el.get("analysisReleasedDate"),el.get("resultCollectionDate"),el.get("resultDeliveryDate"),
					el.get("tat1"),el.get("tat2"),el.get("tat3")
				};
				try {
					csvPrinter.printRecord(d);
				} catch (IOException e) {
					log.error("fail to print data in CSV file: ", e);
				}
			});

			csvPrinter.close(true);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			log.error("fail to print data to CSV file: ", e);
			return null;
		}
	}

}
