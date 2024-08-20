/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.dao.SampleRepository;
import org.itech.labSampleTracker.entities.Sample;
import org.itech.labSampleTracker.enums.ESampleStatus;
import org.itech.labSampleTracker.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * <h2>SampleServiceimpl</h2>
 */
@Service
@Transactional
public class SampleServiceImpl implements SampleService {

	@Autowired
	private SampleRepository sampleRepo;

	@PersistenceContext
	private EntityManager em;

	@Override
	public Sample create(Sample d) {

		Sample entity;

		try {
			entity = sampleRepo.save(d);

		} catch (Exception ex) {
			return null;
		}
		return entity;
	}

	@Override
	public Sample update(Sample d) {
		Sample c;

		try {
			c = sampleRepo.saveAndFlush(d);

		} catch (Exception ex) {
			return null;
		}
		return c;
	}

	@Override
	public Sample getOne(int id) {
		Sample t;

		try {
			t = sampleRepo.findById(id).orElse(null);

		} catch (Exception ex) {
			return null;
		}
		return t;
	}

	@Override
	public List<Sample> getAll() {
		List<Sample> lst;

		try {
			lst = sampleRepo.findAll();

		} catch (Exception ex) {
			return Collections.emptyList();
		}
		return lst;
	}

	@Override
	public long getTotal() {
		long total;

		try {
			total = sampleRepo.count();
		} catch (Exception ex) {
			return 0;
		}
		return total;
	}

	@Override
	public boolean delete(int id) {
		try {
			sampleRepo.deleteById(id);
			return true;

		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public Sample findBySampleIdentifier(String identifier) {
		Sample t;

		try {
			t = sampleRepo.findBySampleIdentifier(identifier);

		} catch (Exception ex) {
			return null;
		}
		return t;
	}

	@Override
	public List<Map<String, Object>> getSampleInTransitForUser(Integer userId) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select auhc.app_user_id, s.id,c.id ,sr.id,sr2.id,sr2.comment,  "
				+ "sr.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number , s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status, s.destination_lab_id,"
				+ "s.requester_site_name,s.destination_lab_name,s.analysis_completed_date, "
				+ "s.analysis_released_date, s.result_reported_date, s.collection_start_mileage, s.collection_end_mileage,"
				+ " s.result_start_mileage,s.result_end_mileage from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id  "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join circuit_site cs on cs.site_id  = sr.site_id join circuit c on c.id = cs.circuit_id  "
				+ "join app_user_has_circuit auhc on auhc.circuit_id  = cs.circuit_id   "
				+ "where ss2.status = :status and auhc.app_user_id = :userId order by s.collection_date DESC";

		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("userId", userId);
			query.setParameter("status", ESampleStatus.ON_TRANSIT.name());
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("circuitNumber", o[11]);
				map.put("collectionDate", dateFormat.format(o[12]));
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);

				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getSampleAtHubForAcceptance(Integer hubId) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select auhs.app_user_id, s.id,cs.circuit_id ,sr.id,sr2.id,sr2.comment,  "
				+ "auhs.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number , s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status, s.destination_lab_id,"
				+ "s.requester_site_name,s.destination_lab_name,s.analysis_completed_date, "
				+ "s.analysis_released_date, s.result_reported_date , s.collection_start_mileage, s.collection_end_mileage,"
				+ "  s.result_start_mileage,s.result_end_mileage  from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id  "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join app_user_has_site auhs on auhs.app_user_id = sr.app_user_id  "
				+ "join circuit_site cs on cs.site_id = auhs.site_id  join circuit c on c.id = cs.circuit_id  "
				+ "where ss2.status = :status and s.hub_id = :hubId order by s.collection_date DESC";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("hubId", hubId);
			query.setParameter("status", ESampleStatus.RECEIVED_AT_HUB.name());
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("circuitNumber", o[11]);
				map.put("collectionDate", ObjectUtils.isNotEmpty(o[12]) ? dateFormat.format(o[12]) : null);
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getAcceptedSampleAtHub(Integer hubId) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select sr.app_user_id, s.id,cs.circuit_id ,sr.id,sr2.id,sr2.comment,  "
				+ "sr.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number ,s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status , s.destination_lab_id,"
				+ "s.requester_site_name,s.destination_lab_name,s.analysis_completed_date, "
				+ "s.analysis_released_date, s.result_reported_date , s.collection_start_mileage, s.collection_end_mileage,"
				+ "  s.result_start_mileage,s.result_end_mileage  from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join circuit_site cs on cs.site_id = sr.site_id join circuit c on c.id = cs.circuit_id  "
				+ "where ss2.status = :status and s.reference_lab_id = :labId order by s.collection_date DESC";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("hubId", hubId);
			query.setParameter("status", ESampleStatus.ACCEPTED_AT_HUB.name());
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("circuitNumber", o[11]);
				map.put("collectionDate", ObjectUtils.isNotEmpty(o[12]) ? dateFormat.format(o[12]) : null);
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getSampleAtLabForAcceptance(Integer labId, String status) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select sr.app_user_id, s.id,cs.circuit_id ,sr.id,sr2.id,sr2.comment,  "
				+ "sr.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number ,s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status , s.destination_lab_id,"
				+ "s.requester_site_name,s.destination_lab_name,s.analysis_completed_date, "
				+ "s.analysis_released_date, s.result_reported_date , s.collection_start_mileage, s.collection_end_mileage, "
				+ " s.result_start_mileage,s.result_end_mileage  from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join circuit_site cs on cs.site_id = sr.site_id join circuit c on c.id = cs.circuit_id  "
				+ "where ss2.status = :status and s.reference_lab_id = :labId order by s.collection_date DESC";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("labId", labId);
			query.setParameter("status", status);
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("circuitNumber", o[11]);
				map.put("collectionDate", ObjectUtils.isNotEmpty(o[12]) ? dateFormat.format(o[12]) : null);
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getAcceptedSampleAtLab(Integer labId, String status) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select sr.app_user_id, s.id,cs.circuit_id ,sr.id,sr2.id,sr2.comment,  "
				+ "sr.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number ,s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status , s.destination_lab_id,"
				+ "s.requester_site_name,s.destination_lab_name,s.analysis_completed_date, "
				+ "s.analysis_released_date, s.result_reported_date , s.collection_start_mileage, s.collection_end_mileage,"
				+ "  s.result_start_mileage,s.result_end_mileage  from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join circuit_site cs on cs.site_id = sr.site_id join circuit c on c.id = cs.circuit_id  "
				+ "where ss2.status = :status and s.reference_lab_id = :labId order by s.collection_date DESC";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("labId", labId);
			query.setParameter("status", status);
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("collectionDate", ObjectUtils.isNotEmpty(o[12]) ? dateFormat.format(o[12]) : null);
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getAnalysisDoneAtLab(Integer labId) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select sr.app_user_id, s.id,cs.circuit_id ,sr.id,sr2.id,sr2.comment,  "
				+ "sr.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number , s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status, s.destination_lab_id, "
				+ " s.requester_site_name, s.destination_lab_name,s.analysis_completed_date,  "
				+ "	s.analysis_released_date, s.result_reported_date , s.collection_start_mileage, s.collection_end_mileage, "
				+ " s.result_start_mileage,s.result_end_mileage  from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id  "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join circuit_site cs on cs.site_id = sr.site_id  join circuit c on c.id = cs.circuit_id  "
				+ "where ss2.status = :status and s.reference_lab_id = :labId order by s.collection_date desc";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("labId", labId);
			query.setParameter("status", ESampleStatus.ANALYSIS_DONE.name());
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("circuitNumber", o[11]);
				map.put("collectionDate", ObjectUtils.isNotEmpty(o[12]) ? dateFormat.format(o[12]) : null);
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getAnalysisFailedAtLab(Integer labId) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select auhs.app_user_id, s.id,cs.circuit_id ,sr.id,sr2.id,sr2.comment,  "
				+ "auhs.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number , s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status, s.destination_lab_id,"
				+ "s.requester_site_name,s.destination_lab_name,s.analysis_completed_date, "
				+ "s.analysis_released_date, s.result_reported_date , s.collection_start_mileage, "
				+ "s.collection_end_mileage, s.result_start_mileage,s.result_end_mileage  from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id  "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join app_user_has_site auhs on auhs.app_user_id = sr.app_user_id  "
				+ "join circuit_site cs on cs.site_id = auhs.site_id  join circuit c on c.id = cs.circuit_id  "
				+ "where ss2.status = :status and s.reference_lab_id = :labId order by s.collection_date DESC";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("labId", labId);
			query.setParameter("status", ESampleStatus.ANALYSIS_FAILED.name());
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("circuitNumber", o[11]);
				map.put("collectionDate", ObjectUtils.isNotEmpty(o[12]) ? dateFormat.format(o[12]) : null);
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getPendingResultAtLab(Integer labId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getSampleCountByStatus() {
		StringBuffer sqlQuery = new StringBuffer("");
		sqlQuery.append(
				"select  ss.status, count(s.id) from sample s right join sample_status ss on s.sample_status_id = ss.id");

		sqlQuery.append("group by ss.status");
		int all, inTransit, delivered, rejected;
		all = inTransit = delivered = rejected = 0;

		Map<String, Object> response = new HashMap<String, Object>();
		try {
			Query query = em.createNativeQuery(sqlQuery.toString());
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				all += Integer.parseInt(o[1].toString());
				if (o[0].toString().equalsIgnoreCase("ON_TRANSIT")) {
					inTransit += Integer.parseInt(o[1].toString());
				}
				if (o[0].toString().equalsIgnoreCase("RECEIVED_AT_REFERENCE_LAB")
						|| o[0].toString().equalsIgnoreCase("RECEIVED_AT_DISTRICT_LAB")
						|| o[0].toString().equalsIgnoreCase("RECEIVED_AT_TB_LAB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_TB_LAB")
						|| o[0].toString().equalsIgnoreCase("RECEIVED_AT_HUB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_REFERENCE_LAB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_DISTRICT_LAB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_TB_LAB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_HUB")
						|| o[0].toString().equalsIgnoreCase("NON_CONFORM")
						|| o[0].toString().equalsIgnoreCase("RESULT_COLLECTED")
						|| o[0].toString().equalsIgnoreCase("RESULT_ON_SITE")
						|| o[0].toString().equalsIgnoreCase("ANALYSIS_FAILED")
						|| o[0].toString().equalsIgnoreCase("ANALYSIS_DONE")) {
					delivered += Integer.parseInt(o[1].toString());
				}
				if (o[0].toString().equalsIgnoreCase("NON_CONFORM")) {
					rejected += Integer.parseInt(o[1].toString());
				}
			}
			response.put("all", all);
			response.put("inTransit", inTransit);
			response.put("delivered", delivered);
			response.put("rejected", rejected);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Map<String, Object> getSampleCountByStatus(Integer regionId, Integer districtId, Integer siteId,
			Date startDate, Date endDate) {
		StringBuffer sqlQuery = new StringBuffer("");
		sqlQuery.append(
				"select  ss.status, count(s.id) from sample s right join sample_status ss on s.sample_status_id = ss.id");
		if (ObjectUtils.isNotEmpty(siteId)) {
			sqlQuery.append(
					" join sample_retrieving sr on sr.id = s.sample_retrieving_id join site on site.id = sr.site_id");
		} else if (ObjectUtils.isNotEmpty(districtId) || ObjectUtils.isNotEmpty(regionId)) {
			sqlQuery.append(
					" join sample_retrieving sr on sr.id = s.sample_retrieving_id join site on site.id = sr.site_id join district d on d.id = site.district_id");
		}
		sqlQuery.append(" where true ");
		if (ObjectUtils.isNotEmpty(siteId)) {
			sqlQuery.append(" and site.id = :siteId");
		} else if (ObjectUtils.isNotEmpty(districtId)) {
			sqlQuery.append(" and d.id = :districtId");
		} else if (ObjectUtils.isNotEmpty(regionId)) {
			sqlQuery.append(" and d.region_id = :regionId");
		}
		if (ObjectUtils.isNotEmpty(startDate) || ObjectUtils.isNotEmpty(endDate)) {
			sqlQuery.append(" and s.collection_date between :startDate and :endDate");
		}
		sqlQuery.append(" group by ss.status");
		int all, inTransit, delivered, rejected;
		all = inTransit = delivered = rejected = 0;

		Map<String, Object> response = new HashMap<String, Object>();
		try {
			Query query = em.createNativeQuery(sqlQuery.toString());
			if (ObjectUtils.isNotEmpty(siteId)) {
				query.setParameter("siteId", siteId);
			} else if (ObjectUtils.isNotEmpty(districtId)) {
				query.setParameter("districtId", districtId);
			} else if (ObjectUtils.isNotEmpty(regionId)) {
				sqlQuery.append(" and d.region_id = :regionId");
				query.setParameter("regionId", regionId);
			}
			if (ObjectUtils.isNotEmpty(startDate) || ObjectUtils.isNotEmpty(endDate)) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				all += Integer.parseInt(o[1].toString());
				if (o[0].toString().equalsIgnoreCase("ON_TRANSIT")) {
					inTransit += Integer.parseInt(o[1].toString());
				}
				if (o[0].toString().equalsIgnoreCase("RECEIVED_AT_REFERENCE_LAB")
						|| o[0].toString().equalsIgnoreCase("RECEIVED_AT_DISTRICT_LAB")
						|| o[0].toString().equalsIgnoreCase("RECEIVED_AT_TB_LAB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_TB_LAB")
						|| o[0].toString().equalsIgnoreCase("RECEIVED_AT_HUB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_REFERENCE_LAB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_DISTRICT_LAB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_TB_LAB")
						|| o[0].toString().equalsIgnoreCase("ACCEPTED_AT_HUB")
						|| o[0].toString().equalsIgnoreCase("NON_CONFORM")
						|| o[0].toString().equalsIgnoreCase("RESULT_COLLECTED")
						|| o[0].toString().equalsIgnoreCase("RESULT_ON_SITE")
						|| o[0].toString().equalsIgnoreCase("ANALYSIS_FAILED")
						|| o[0].toString().equalsIgnoreCase("ANALYSIS_DONE")) {
					delivered += Integer.parseInt(o[1].toString());
				}
				if (o[0].toString().equalsIgnoreCase("NON_CONFORM")) {
					rejected += Integer.parseInt(o[1].toString());
				}
			}
			response.put("all", all);
			response.put("inTransit", inTransit);
			response.put("delivered", delivered);
			response.put("rejected", rejected);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getSampleDetails() {
		String sql = "SELECT s.id, d.name AS district, site.name AS site, "
				+ "    st.name AS sample_type, s.sample_identifier, s.patient_identifier, "
				+ "    s.collection_date, destination_lab.lab_name AS destination_lab, "
				+ "    ss2.description, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
				+ "    COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) AS reception_date, "
				+ "    s.lab_number, " + "    (s.collection_end_mileage - s.collection_start_mileage) distance, "
				+ "    analysis_completed_date, analysis_released_date, result_collection_date, "
				+ "    result_delivery_date, "
				+ "    DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat,  "
				+ "  sr2.comment FROM sample s JOIN sample_type st ON st.id = s.sample_type_id JOIN "
				+ "    sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
				+ "    sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
				+ "    site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id  "
				+ " LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id  "
				+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN "
				+ "    lab hub ON hub.id = s.hub_id LEFT JOIN "
				+ "    lab destination_lab ON destination_lab.id = s.destination_lab_id ";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("sampleId", o[0]);
				map.put("district", o[1]);
				map.put("site", o[2]);
				map.put("sampleType", o[3]);
				map.put("sampleIdentifier", o[4]);
				map.put("patientIdentifier", o[5]);
				map.put("collectionDate", o[6]);
				map.put("destinationLab", o[7]);
				map.put("status", o[8]);
				map.put("lab", o[9]);
				map.put("receptionDate", o[10]);
				map.put("labNumber", o[11]);
				map.put("distance", o[12]);
				map.put("analysisCompletedDate", o[13]);
				map.put("analysisReleasedDate", o[14]);
				map.put("resultCollectionDate", o[15]);
				map.put("resultDeliveryDate", o[16]);
				map.put("TAT", o[17]);
				map.put("rejectionComment", o[18]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Map<String, Map<String, Integer>> getSampleStatusBySampleType(Integer regionId, Integer districtId,
			Integer siteId, Date startDate, Date endDate) {
		StringBuffer sql = new StringBuffer();
		sql.append("select st.name sample_type, ss2.status, count(s.id) from sample s  "
				+ " JOIN sample_type st on st.id =s.sample_type_id  "
				+ " JOIN sample_status ss2 on ss2.id = s.sample_status_id "
				+ " JOIN sample_retrieving sr ON s.sample_retrieving_id = sr.id "
				+ " JOIN site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id WHERE true ");
		if (ObjectUtils.isNotEmpty(regionId))
			sql.append(" AND d.region_id = :regionId ");
		if (ObjectUtils.isNotEmpty(districtId))
			sql.append(" AND d.id = :districtId ");
		if (ObjectUtils.isNotEmpty(siteId))
			sql.append(" AND site.id = :siteId ");
		sql.append(" AND (s.collection_date between :startDate and :endDate) ");
		sql.append(" group by st.name, ss2.status ");
		Map<String, Map<String, Integer>> response = new LinkedHashMap<String, Map<String, Integer>>();
		Map<String, Integer> biItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> bsItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> cvItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> eidItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> tbItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> hpvItem = new LinkedHashMap<String, Integer>();
		biItem.put("COLLECTES", 0);
		biItem.put("TRANSMIS", 0);
		biItem.put("NON CONFORME", 0);
		biItem.put("ECHEC", 0);

		bsItem.put("COLLECTES", 0);
		bsItem.put("TRANSMIS", 0);
		bsItem.put("NON CONFORME", 0);
		bsItem.put("ECHEC", 0);

		cvItem.put("COLLECTES", 0);
		cvItem.put("TRANSMIS", 0);
		cvItem.put("NON CONFORME", 0);
		cvItem.put("ECHEC", 0);

		eidItem.put("COLLECTES", 0);
		eidItem.put("TRANSMIS", 0);
		eidItem.put("NON CONFORME", 0);
		eidItem.put("ECHEC", 0);

		tbItem.put("COLLECTES", 0);
		tbItem.put("TRANSMIS", 0);
		tbItem.put("NON CONFORME", 0);
		tbItem.put("ECHEC", 0);

		hpvItem.put("COLLECTES", 0);
		hpvItem.put("TRANSMIS", 0);
		hpvItem.put("NON CONFORME", 0);
		hpvItem.put("ECHEC", 0);

		response.put("BI", biItem);
		response.put("BS", bsItem);
		response.put("CV", cvItem);
		response.put("EID", eidItem);
		response.put("TB", tbItem);
		response.put("HPV", hpvItem);
		List<String> transmisStatusList = Arrays.asList(new String[] { "ACCEPTED_AT_REFERENCE_LAB",
				"ACCEPTED_AT_TB_LAB", "ACCEPTED_AT_DISTRICT_LAB", "ACCEPTED_AT_HUB", "NON_CONFORM", "RESULT_COLLECTED",
				"RESULT_ON_SITE", "RECEIVED_AT_REFERENCE_LAB", "RECEIVED_AT_TB_LAB", "RECEIVED_AT_DISTRICT_LAB",
				"ANALYSIS_FAILED", "ANALYSIS_DONE", "RECEIVED_AT_HUB" });
		try {
			Query query = em.createNativeQuery(sql.toString());
			if (ObjectUtils.isNotEmpty(regionId))
				query.setParameter("regionId", regionId);
			if (ObjectUtils.isNotEmpty(districtId))
				query.setParameter("districtId", districtId);
			if (ObjectUtils.isNotEmpty(siteId))
				query.setParameter("siteId", siteId);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				String entry = o[0].toString();
				Map<String, Integer> map = response.get(entry);
				map.replace("COLLECTES", map.get("COLLECTES") + Integer.parseInt(o[2].toString()));
				if (transmisStatusList.contains(o[1].toString())) {
					map.replace("TRANSMIS", map.get("TRANSMIS") + Integer.parseInt(o[2].toString()));
				}
				if (o[1].toString().equalsIgnoreCase("NON_CONFORM")) {
					map.replace("NON CONFORME", map.get("NON CONFORME") + Integer.parseInt(o[2].toString()));
				}
				if (o[1].toString().equalsIgnoreCase("ANALYSIS_FAILED")) {
					map.replace("ECHEC", map.get("ECHEC") + Integer.parseInt(o[2].toString()));
				}
				response.replace(entry, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Map<String, Map<String, Integer>> getSampleStatusBySampleType() {
		String sql = "select st.name sample_type, ss2.status, count(s.id) from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id group by st.name, ss2.status ";
		Map<String, Map<String, Integer>> response = new LinkedHashMap<String, Map<String, Integer>>();
		Map<String, Integer> biItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> bsItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> cvItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> eidItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> tbItem = new LinkedHashMap<String, Integer>();
		Map<String, Integer> hpvItem = new LinkedHashMap<String, Integer>();
		biItem.put("COLLECTES", 0);
		biItem.put("TRANSMIS", 0);
		biItem.put("NON CONFORME", 0);
		biItem.put("ECHEC", 0);

		bsItem.put("COLLECTES", 0);
		bsItem.put("TRANSMIS", 0);
		bsItem.put("NON CONFORME", 0);
		bsItem.put("ECHEC", 0);

		cvItem.put("COLLECTES", 0);
		cvItem.put("TRANSMIS", 0);
		cvItem.put("NON CONFORME", 0);
		cvItem.put("ECHEC", 0);

		eidItem.put("COLLECTES", 0);
		eidItem.put("TRANSMIS", 0);
		eidItem.put("NON CONFORME", 0);
		eidItem.put("ECHEC", 0);

		tbItem.put("COLLECTES", 0);
		tbItem.put("TRANSMIS", 0);
		tbItem.put("NON CONFORME", 0);
		tbItem.put("ECHEC", 0);

		hpvItem.put("COLLECTES", 0);
		hpvItem.put("TRANSMIS", 0);
		hpvItem.put("NON CONFORME", 0);
		hpvItem.put("ECHEC", 0);

		response.put("BI", biItem);
		response.put("BS", bsItem);
		response.put("CV", cvItem);
		response.put("EID", eidItem);
		response.put("TB", tbItem);
		response.put("HPV", hpvItem);
		List<String> transmisStatusList = Arrays.asList(new String[] { "ACCEPTED_AT_REFERENCE_LAB",
				"ACCEPTED_AT_TB_LAB", "ACCEPTED_AT_DISTRICT_LAB", "ACCEPTED_AT_HUB", "NON_CONFORM", "RESULT_COLLECTED",
				"RESULT_ON_SITE", "RECEIVED_AT_REFERENCE_LAB", "RECEIVED_AT_TB_LAB", "RECEIVED_AT_DISTRICT_LAB",
				"ANALYSIS_FAILED", "ANALYSIS_DONE", "RECEIVED_AT_HUB" });
		try {
			Query query = em.createNativeQuery(sql);
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				String entry = o[0].toString();
				Map<String, Integer> map = response.get(entry);
				map.replace("COLLECTES", map.get("COLLECTES") + Integer.parseInt(o[2].toString()));
				if (transmisStatusList.contains(o[1].toString())) {
					map.replace("TRANSMIS", map.get("TRANSMIS") + Integer.parseInt(o[2].toString()));
				}
				if (o[1].toString().equalsIgnoreCase("NON_CONFORM")) {
					map.replace("NON CONFORME", map.get("NON CONFORME") + Integer.parseInt(o[2].toString()));
				}
				if (o[1].toString().equalsIgnoreCase("ANALYSIS_FAILED")) {
					map.replace("ECHEC", map.get("ECHEC") + Integer.parseInt(o[2].toString()));
				}
				response.replace(entry, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getSampleInTransitForUserAndLab(Integer userId, Integer labId) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select auhc.app_user_id, s.id,c.id ,sr.id,sr2.id,sr2.comment,  "
				+ "sr.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number , s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status, s.destination_lab_id,"
				+ "s.requester_site_name,s.destination_lab_name,s.analysis_completed_date, "
				+ "s.analysis_released_date, s.result_reported_date , s.collection_start_mileage, s.collection_end_mileage,"
				+ " s.result_start_mileage,s.result_end_mileage  from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id  "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join circuit_site cs on cs.site_id  = sr.site_id join circuit c on c.id = cs.circuit_id  "
				+ "join app_user_has_circuit auhc on auhc.circuit_id  = cs.circuit_id   "
				+ "where (s.destination_lab_id = :labId or s.destination_lab_id is null) "
				+ " and ss2.status = :status and auhc.app_user_id = :userId order by s.collection_date DESC";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("userId", userId);
			query.setParameter("status", ESampleStatus.ON_TRANSIT.name());
			query.setParameter("labId", labId);
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("circuitNumber", o[11]);
				map.put("collectionDate", dateFormat.format(o[12]));
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getSampleResultForUserAndSite(Integer userId, Integer siteId) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select auhc.app_user_id, s.id,c.id ,sr.id,sr2.id,sr2.comment,  "
				+ "sr.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number , s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status , s.destination_lab_id,"
				+ "s.requester_site_name,s.destination_lab_name,s.analysis_completed_date, "
				+ "s.analysis_released_date, s.result_reported_date , s.collection_start_mileage, s.collection_end_mileage,"
				+ " s.result_start_mileage,s.result_end_mileage  from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id  "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join circuit_site cs on cs.site_id  = sr.site_id join circuit c on c.id = cs.circuit_id  "
				+ "join app_user_has_circuit auhc on auhc.circuit_id  = cs.circuit_id   "
				+ "where sr.site_id = :siteId and ss2.status = :status and auhc.app_user_id = :userId order by s.collection_date DESC";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("userId", userId);
			query.setParameter("status", ESampleStatus.RESULT_COLLECTED.name());
			query.setParameter("siteId", siteId);
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("circuitNumber", o[11]);
				map.put("collectionDate", dateFormat.format(o[12]));
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<Map<String, Object>> getSampleInTransitForUserAndLab(Integer userId, Integer labId,
			List<String> sampleTypes) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String sql = "select auhc.app_user_id, s.id,c.id ,sr.id,sr2.id,sr2.comment,  "
				+ "sr.site_id, s.hub_id, s.reference_lab_id ,s.patient_identifier, s.sample_identifier,  "
				+ "c.circuit_number , s.collection_date , s.deliver_at_hub_date, s.deliver_at_lab_date , "
				+ "s.accepted_at_hub_date , s.accepted_at_lab_date , st.name ,s.lab_number , "
				+ "s.result_collection_date , s.result_delivery_date , ss2.status, s.destination_lab_id,"
				+ "s.requester_site_name,s.destination_lab_name,s.analysis_completed_date, "
				+ "s.analysis_released_date, s.result_reported_date , s.collection_start_mileage, s.collection_end_mileage,"
				+ " s.result_start_mileage,s.result_end_mileage  from sample s  "
				+ "join sample_type st on st.id =s.sample_type_id  "
				+ "join sample_status ss2 on ss2.id = s.sample_status_id  "
				+ "left join sample_rejection sr2 on sr2.sample_id = s.id "
				+ "join sample_retrieving sr on s.sample_retrieving_id  = sr.id  "
				+ "join circuit_site cs on cs.site_id  = sr.site_id join circuit c on c.id = cs.circuit_id  "
				+ "join app_user_has_circuit auhc on auhc.circuit_id  = cs.circuit_id   "
				+ "where (s.destination_lab_id = :labId or s.destination_lab_id is null) and ss2.status = :status "
				+ " and auhc.app_user_id = :userId and st.name in (:sampleTypes) order by s.collection_date DESC";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			query.setParameter("userId", userId);
			query.setParameter("status", ESampleStatus.ON_TRANSIT.name());
			query.setParameter("labId", labId);
			query.setParameter("sampleTypes", sampleTypes);
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", o[0]);
				map.put("sampleId", o[1]);
				map.put("circuitId", o[2]);
				map.put("sampleRetrievingId", o[3]);
				map.put("rejectionTypeId", o[4]);
				map.put("rejectionComment", o[5]);
				map.put("siteId", o[6]);
				map.put("hubId", o[7]);
				map.put("labId", o[8]);
				map.put("patientIdentifier", o[9]);
				map.put("sampleIdentifier", o[10]);
				map.put("circuitNumber", o[11]);
				map.put("collectionDate", dateFormat.format(o[12]));
				map.put("deliveredAtHubDate", ObjectUtils.isNotEmpty(o[13]) ? dateFormat.format(o[13]) : null);
				map.put("deliveredAtReferenceLabDate", ObjectUtils.isNotEmpty(o[14]) ? dateFormat.format(o[14]) : null);
				map.put("acceptedAtHubDate", ObjectUtils.isNotEmpty(o[15]) ? dateFormat.format(o[15]) : null);
				map.put("acceptedAtReferenceLabDate", ObjectUtils.isNotEmpty(o[16]) ? dateFormat.format(o[16]) : null);
				map.put("sampleType", o[17]);
				map.put("labNumber", o[18]);
				map.put("resultCollectionDate", ObjectUtils.isNotEmpty(o[19]) ? dateFormat.format(o[19]) : null);
				map.put("resultDeliveryDate", ObjectUtils.isNotEmpty(o[20]) ? dateFormat.format(o[20]) : null);
				map.put("status", o[21]);
				map.put("destinationLabId", o[22]);
				map.put("requesterSiteName", o[23]);
				map.put("destinationLabName", o[24]);
				map.put("analysisCompletedDate", ObjectUtils.isNotEmpty(o[25]) ? dateFormat.format(o[25]) : null);
				map.put("analysisReleasedDate", ObjectUtils.isNotEmpty(o[26]) ? dateFormat.format(o[26]) : null);
				map.put("analysisResultReportedDate", ObjectUtils.isNotEmpty(o[27]) ? dateFormat.format(o[27]) : null);
				map.put("collectionStartMileage", o[28]);
				map.put("collectionEndMileage", o[29]);
				map.put("resultStartMileage", o[30]);
				map.put("resultEndMileage", o[31]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Map<String, Object> getSampleCountByStatus(Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getSampleCountByStatusForRegion(Integer regionId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getSampleCountByStatusForDistrict(Integer districtId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getSampleCountByStatusForSite(Integer siteId, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Map<String, Object>> getSampleDetails(Pageable pageable, Integer regionId, Integer districtId,
			Integer siteId, Date startDate, Date endDate, Integer status) {

		if (ObjectUtils.isNotEmpty(regionId)) {
			if (ObjectUtils.isNotEmpty(startDate) && ObjectUtils.isNotEmpty(endDate)) {
				return sampleRepo.getSampleListByDateAndRegion(pageable, startDate, endDate, regionId, status);
			}
			return sampleRepo.getSampleListByRegion(pageable, regionId, status);
		}
		if (ObjectUtils.isNotEmpty(districtId)) {
			if (ObjectUtils.isNotEmpty(startDate) && ObjectUtils.isNotEmpty(endDate)) {
				return sampleRepo.getSampleListByDateAndDistrict(pageable, startDate, endDate, districtId, status);
			}
			return sampleRepo.getSampleListByDistrict(pageable, districtId, status);
		}
		if (ObjectUtils.isNotEmpty(siteId)) {
			if (ObjectUtils.isNotEmpty(startDate) && ObjectUtils.isNotEmpty(endDate)) {
				return sampleRepo.getSampleListByDateAndSite(pageable, startDate, endDate, siteId, status);
			}
			return sampleRepo.getSampleListBySite(pageable, siteId, status);
		}
		if (ObjectUtils.isNotEmpty(startDate) && ObjectUtils.isNotEmpty(endDate)) {
			return sampleRepo.getSampleListByDate(pageable, startDate, endDate, status);
		}
		return sampleRepo.getSampleList(pageable, status);
	}

	@Override
	public boolean removeById(Integer d) {
		try {
			sampleRepo.deleteById(d);
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	@Override
	public List<Map<String, String>> getAll(Integer region, Integer district, Integer site, Date startDate,
			Date endDate, Integer status) {
		StringBuffer sql = new StringBuffer();
		sql.append(
				" SELECT s.id,reg.name AS region, d.name AS district, site.name AS site, concat(au.last_name,' ', au.first_name) rider, "
						+ "st.name AS sample_type, s.patient_identifier, to_char(s.collection_date,'dd/MM/yyyy HH24:MI') collection_date,  "
						+ "destination_lab.lab_name AS destination_lab, "
						+ "ss2.description status, sr2.comment rejection_comment, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
						+ "to_char(COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date),'dd/MM/yyyy HH24:MI') AS reception_date, s.lab_number, "
						+ "(s.collection_end_mileage - s.collection_start_mileage) distance, "
						+ "to_char(analysis_completed_date,'dd/MM/yyyy HH24:MI') analysis_completed_date,  "
						+ "to_char(analysis_released_date,'dd/MM/yyyy HH24:MI') analysis_released_date, to_char(result_collection_date,'dd/MM/yyyy HH24:MI') result_collection_date, "
						+ "to_char(result_delivery_date,'dd/MM/yyyy HH24:MI') result_delivery_date, "
						+ "DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat1, "
						+ "DATE_PART('DAY', COALESCE(s.result_reported_date, now()) - COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date,now())) AS tat2, "
						+ "DATE_PART('DAY', COALESCE(s.result_delivery_date, now()) - COALESCE(s.result_collection_date, now()) ) AS tat3 "
						+ "FROM sample s JOIN sample_type st ON st.id = s.sample_type_id  "
						+ "join sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
						+ "sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
						+ "site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
						+ " JOIN region reg ON reg.id = d.region_id LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id "
						+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id LEFT JOIN "
						+ "lab destination_lab ON destination_lab.id = s.destination_lab_id  "
						+ "join app_user au on au.id = sr.app_user_id where true ");
		if (ObjectUtils.isNotEmpty(region))
			sql.append(" AND reg.id = :regionId ");
		if (ObjectUtils.isNotEmpty(district))
			sql.append(" AND d.id = :districtId ");
		if (ObjectUtils.isNotEmpty(site))
			sql.append(" AND site.id = :siteId ");
		if (ObjectUtils.isNotEmpty(status))
			sql.append("AND (:status IS NULL OR ss2.id = :status)");
		sql.append(" AND (s.collection_date between :startDate and :endDate) ");

		List<Map<String, String>> response = new ArrayList<Map<String, String>>();
		try {
			Query query = em.createNativeQuery(sql.toString());
			if (ObjectUtils.isNotEmpty(region))
				query.setParameter("regionId", region);
			if (ObjectUtils.isNotEmpty(district))
				query.setParameter("districtId", district);
			if (ObjectUtils.isNotEmpty(site))
				query.setParameter("siteId", site);
			if (ObjectUtils.isNotEmpty(status))
				query.setParameter("status", status);
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);

			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("sampleId", Objects.toString(o[0], null));
				map.put("region", Objects.toString(o[1], null));
				map.put("district", Objects.toString(o[2], null));
				map.put("site", Objects.toString(o[3], null));
				map.put("rider", Objects.toString(o[4], null));
				map.put("sampleType", Objects.toString(o[5], null));
				map.put("patientIdentifier", Objects.toString(o[6], null));
				map.put("collectionDate", Objects.toString(o[7], null));
				map.put("destinationLab", Objects.toString(o[8], null));
				map.put("status", Objects.toString(o[9], null));
				map.put("rejectionComment", Objects.toString(o[10], null));
				map.put("lab", Objects.toString(o[11], null));
				map.put("receptionDate", Objects.toString(o[12], null));
				map.put("labNumber", Objects.toString(o[13], null));
				map.put("distance", Objects.toString(o[14], null));
				map.put("analysisCompletedDate", Objects.toString(o[15], null));
				map.put("analysisReleasedDate", Objects.toString(o[16], null));
				map.put("resultCollectionDate", Objects.toString(o[17], null));
				map.put("resultDeliveryDate", Objects.toString(o[18], null));
				map.put("tat1", Objects.toString(o[19], null));
				map.put("tat2", Objects.toString(o[20], null));
				map.put("tat3", Objects.toString(o[21], null));
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
