package org.itech.labSampleTracker.service_impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.dto.RiderReportItem;
import org.itech.labSampleTracker.entities.AppUser;
import org.itech.labSampleTracker.helper.Utils;
import org.itech.labSampleTracker.service.AppUserService;
import org.itech.labSampleTracker.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Slf4j
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private AppUserService appUserService;

	@Override
	public byte[] getReportItems(Integer regionId, Integer districtId, Integer siteId, Integer labId, Integer riderId,
			Date startDate, Date endDate, String reportName) {
		JasperReport jasperReport;

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			jasperReport = (JasperReport) JRLoader
					.loadObject(ResourceUtils.getURL("classpath:reports/" + reportName).openStream());
			Map<String, Object> parameters = new HashMap<>();
			AppUser rider = appUserService.findUserById(riderId).orElse(new AppUser());
			parameters.put("rider_name", rider.getLastName() + " " + rider.getFirstName());
			parameters.put("start_date", sdf.format(startDate));
			parameters.put("end_date", sdf.format(endDate));

			RiderReportItem items = new RiderReportItem();

			// get data from db
			List<Map<String, Object>> rawData = getReportRawValues(startDate, endDate, regionId, districtId, siteId,
					labId, riderId);
			for (Map<String, Object> d : rawData) {
				// collected
				if (d.get("sample_type").toString().equalsIgnoreCase("CV")) {
					items.setCollectedCVCount(
							items.getCollectedCVCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BI")) {
					items.setCollectedBICount(
							items.getCollectedBICount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BS")) {
					items.setCollectedBSCount(
							items.getCollectedBSCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("EID")) {
					items.setCollectedEIDCount(
							items.getCollectedEIDCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("TB")) {
					items.setCollectedTBCount(
							items.getCollectedTBCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("HPV")) {
					items.setCollectedHPVCount(
							items.getCollectedHPVCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}

				// received_at_lab
				if (d.get("sample_type").toString().equalsIgnoreCase("CV")
						&& d.get("lab_type").toString().equalsIgnoreCase("PLATEFORME")) {
					items.setDeliveredCVCount(
							items.getDeliveredCVCount() + Integer.parseInt(d.get("received_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("CV")
						&& d.get("lab_type").toString().equalsIgnoreCase("RELAIS")) {
					items.setDeliveredHubCVCount(
							items.getDeliveredHubCVCount() + Integer.parseInt(d.get("received_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BI")
						&& d.get("lab_type").toString().equalsIgnoreCase("DISTRICT")) {
					items.setDeliveredBICount(
							items.getDeliveredBICount() + Integer.parseInt(d.get("received_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BS")
						&& d.get("lab_type").toString().equalsIgnoreCase("DISTRICT")) {
					items.setDeliveredBSCount(
							items.getDeliveredBSCount() + Integer.parseInt(d.get("received_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("EID")
						&& d.get("lab_type").toString().equalsIgnoreCase("PLATEFORME")) {
					items.setDeliveredEIDCount(
							items.getDeliveredEIDCount() + Integer.parseInt(d.get("received_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("EID")
						&& d.get("lab_type").toString().equalsIgnoreCase("RELAIS")) {
					items.setDeliveredHubEIDCount(
							items.getDeliveredHubEIDCount() + Integer.parseInt(d.get("received_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("TB")
						&& d.get("lab_type").toString().equalsIgnoreCase("CAT")) {
					items.setDeliveredTBCount(
							items.getDeliveredTBCount() + Integer.parseInt(d.get("received_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("HPV")
						&& d.get("lab_type").toString().equalsIgnoreCase("PLATEFORME")) {
					items.setDeliveredHPVCount(
							items.getDeliveredHPVCount() + Integer.parseInt(d.get("received_at_lab").toString()));
				}

				// non_conforming
				if (d.get("sample_type").toString().equalsIgnoreCase("CV")
						&& d.get("lab_type").toString().equalsIgnoreCase("PLATEFORME")
						&& d.get("status").toString().equalsIgnoreCase("NON_CONFORM")) {
					items.setNcCVCount(items.getNcCVCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("CV")
						&& d.get("lab_type").toString().equalsIgnoreCase("RELAIS")
						&& d.get("status").toString().equalsIgnoreCase("NON_CONFORM")) {
					items.setNcHubCVCount(
							items.getNcHubCVCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BI")
						&& d.get("lab_type").toString().equalsIgnoreCase("DISTRICT")
						&& d.get("status").toString().equalsIgnoreCase("NON_CONFORM")) {
					items.setNcBICount(items.getNcBICount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BS")
						&& d.get("lab_type").toString().equalsIgnoreCase("DISTRICT")
						&& d.get("status").toString().equalsIgnoreCase("NON_CONFORM")) {
					items.setNcBSCount(items.getNcBSCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("EID")
						&& d.get("lab_type").toString().equalsIgnoreCase("PLATEFORME")
						&& d.get("status").toString().equalsIgnoreCase("NON_CONFORM")) {
					items.setNcEIDCount(
							items.getNcEIDCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("EID")
						&& d.get("lab_type").toString().equalsIgnoreCase("RELAIS")
						&& d.get("status").toString().equalsIgnoreCase("NON_CONFORM")) {
					items.setNcHubEIDCount(
							items.getNcHubEIDCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("TB")
						&& d.get("lab_type").toString().equalsIgnoreCase("CAT")
						&& d.get("status").toString().equalsIgnoreCase("NON_CONFORM")) {
					items.setNcTBCount(items.getNcTBCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("HPV")
						&& d.get("lab_type").toString().equalsIgnoreCase("PLATEFORME")
						&& d.get("status").toString().equalsIgnoreCase("NON_CONFORM")) {
					items.setNcHPVCount(
							items.getNcHPVCount() + Integer.parseInt(d.get("collected_on_site").toString()));
				}

				// result collected
				if (d.get("sample_type").toString().equalsIgnoreCase("CV")) {
					items.setResultCollectedCVCount(items.getResultCollectedCVCount()
							+ Integer.parseInt(d.get("result_collected_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BI")) {
					items.setResultCollectedBICount(items.getResultCollectedBICount()
							+ Integer.parseInt(d.get("result_collected_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BS")) {
					items.setResultCollectedBSCount(items.getResultCollectedBSCount()
							+ Integer.parseInt(d.get("result_collected_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("EID")) {
					items.setResultCollectedEIDCount(items.getResultCollectedEIDCount()
							+ Integer.parseInt(d.get("result_collected_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("TB")) {
					items.setResultCollectedTBCount(items.getResultCollectedTBCount()
							+ Integer.parseInt(d.get("result_collected_at_lab").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("HPV")) {
					items.setResultCollectedHPVCount(items.getResultCollectedHPVCount()
							+ Integer.parseInt(d.get("result_collected_at_lab").toString()));
				}

				// result delivered on site
				if (d.get("sample_type").toString().equalsIgnoreCase("CV")) {
					items.setResultDeliveredCVCount(items.getResultDeliveredCVCount()
							+ Integer.parseInt(d.get("result_delivered_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BI")) {
					items.setResultDeliveredBICount(items.getResultDeliveredBICount()
							+ Integer.parseInt(d.get("result_delivered_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("BS")) {
					items.setResultDeliveredBSCount(items.getResultDeliveredBSCount()
							+ Integer.parseInt(d.get("result_delivered_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("EID")) {
					items.setResultDeliveredEIDCount(items.getResultDeliveredEIDCount()
							+ Integer.parseInt(d.get("result_delivered_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("TB")) {
					items.setResultDeliveredTBCount(items.getResultDeliveredTBCount()
							+ Integer.parseInt(d.get("result_delivered_on_site").toString()));
				}
				if (d.get("sample_type").toString().equalsIgnoreCase("HPV")) {
					items.setResultDeliveredHPVCount(items.getResultDeliveredHPVCount()
							+ Integer.parseInt(d.get("result_delivered_on_site").toString()));
				}

			}
			parameters.putAll(Utils.objectToMap(items));

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			byte[] reportContent = JasperExportManager.exportReportToPdf(jasperPrint);
			return reportContent;

		} catch (JRException | IOException | IllegalAccessException ex) {
			ex.printStackTrace();
			log.error("error building report. ", ex);
			return null;
		}
	}

	private List<Map<String, Object>> getReportRawValues(Date startDate, Date endDate, Integer regionId,
			Integer districtId, Integer siteId, Integer labId, Integer userId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ss.status, st.\"name\", l.lab_type,"
				+ " count(case when s.collection_date between :start_date and :end_date then s.id end) as collected_on_site,"
				+ " count(case when s.accepted_at_lab_date between :start_date and :end_date then s.id end) as received_at_lab,"
				+ " count(case when s.result_collection_date between :start_date and :end_date then s.id end) as result_collected_at_lab,"
				+ " count(case when s.result_delivery_date between :start_date and :end_date then s.id end) as result_delivered_on_site"
				+ " from sample s join sample_status ss on ss.id = s.sample_status_id"
				+ " join sample_retrieving sr on sr.id = s.sample_retrieving_id"
				+ " join sample_type st on s.sample_type_id = st.id join app_user au on au.id = sr.app_user_id"
				+ " join site on site.id = sr.site_id join district d on d.id = site.district_id"
				+ " join lab l on l.id = s.destination_lab_id"
				+ " where (s.collection_date between :start_date and :end_date"
				+ " or s.accepted_at_lab_date between :start_date and :end_date"
				+ " or s.result_collection_date between :start_date and :end_date"
				+ " or s.result_delivery_date between :start_date and :end_date)");

		if (ObjectUtils.isNotEmpty(regionId)) {
			sql.append(" AND d.region_id = :regionId");
		}
		if (ObjectUtils.isNotEmpty(districtId)) {
			sql.append(" AND d.id = :districtId");
		}
		if (ObjectUtils.isNotEmpty(siteId)) {
			sql.append(" AND site.id = :siteId");
		}
		if (ObjectUtils.isNotEmpty(labId)) {
			sql.append(" AND l.id = :labId");
		}
		if (ObjectUtils.isNotEmpty(userId)) {
			sql.append(" AND sr.app_user_id = :userId");
		}
		sql.append(" group by ss.status, st.\"name\", l.lab_type");


		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter("start_date", startDate);
			query.setParameter("end_date", endDate);
			if (ObjectUtils.isNotEmpty(regionId)) {
				query.setParameter("regionId", regionId);
			}
			if (ObjectUtils.isNotEmpty(districtId)) {
				query.setParameter("districtId", districtId);
			}
			if (ObjectUtils.isNotEmpty(siteId)) {
				query.setParameter("siteId", siteId);
			}
			if (ObjectUtils.isNotEmpty(labId)) {
				query.setParameter("labId", labId);
			}
			if (ObjectUtils.isNotEmpty(userId)) {
				query.setParameter("userId", userId);
			}
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("status", o[0]);
				map.put("sample_type", o[1]);
				map.put("lab_type", o[2]);
				map.put("collected_on_site", o[3]);
				map.put("received_at_lab", o[4]);
				map.put("result_collected_at_lab", o[5]);
				map.put("result_delivered_on_site", o[6]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
