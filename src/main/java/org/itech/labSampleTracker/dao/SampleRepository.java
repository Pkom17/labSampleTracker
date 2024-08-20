/*
 * Java domain class for entity "Sample" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;

import java.util.Date;
import java.util.Map;

import org.itech.labSampleTracker.entities.Sample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * <h2>SampleRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "Sample" Repository
 */

public interface SampleRepository extends JpaRepository<Sample, Integer>, JpaSpecificationExecutor<Sample> {
	public Sample findBySampleIdentifier(String sampleIdentifier);

	@Query(value = "SELECT s.id, reg.name AS region, d.name AS district, site.name AS site, "
			+ "st.name AS sample_type, s.sample_identifier, s.patient_identifier, "
			+ "s.collection_date, destination_lab.lab_name AS destination_lab, "
			+ "ss2.description status, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
			+ "COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) AS reception_date, s.lab_number, "
			+ "(s.collection_end_mileage - s.collection_start_mileage) distance, "
			+ "analysis_completed_date, analysis_released_date, result_collection_date, result_delivery_date, "
			+ "DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat, "
			+ "sr2.comment rejection_comment  FROM sample s JOIN sample_type st ON st.id = s.sample_type_id JOIN "
			+ "sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
			+ "sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
			+ "site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
			+ " JOIN region reg ON reg.id = d.region_id LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id "
			+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id LEFT JOIN "
			+ "lab destination_lab ON destination_lab.id = s.destination_lab_id WHERE (:status IS NULL OR ss2.id = :status)", countQuery = "SELECT COUNT(s.id) FROM sample s "
					+ "JOIN sample_type st ON st.id = s.sample_type_id "
					+ "JOIN sample_status ss2 ON ss2.id = s.sample_status_id "
					+ "JOIN sample_retrieving sr ON s.sample_retrieving_id = sr.id "
					+ "JOIN site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
					+ "LEFT JOIN sample_rejection sr2 ON sr2.sample_id = s.id "
					+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id "
					+ "LEFT JOIN lab destination_lab ON destination_lab.id = s.destination_lab_id WHERE (:status IS NULL OR ss2.id = :status)", nativeQuery = true)
	Page<Map<String, Object>> getSampleList(Pageable pageable, Integer status);

	@Query(value = "SELECT s.id,reg.name AS region, d.name AS district, site.name AS site, "
			+ "st.name AS sample_type, s.sample_identifier, s.patient_identifier, "
			+ "s.collection_date, destination_lab.lab_name AS destination_lab, "
			+ "ss2.description status, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
			+ "COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) AS reception_date, s.lab_number, "
			+ "(s.collection_end_mileage - s.collection_start_mileage) distance, "
			+ "analysis_completed_date, analysis_released_date, result_collection_date, result_delivery_date, "
			+ "DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat, "
			+ "sr2.comment rejection_comment  FROM sample s JOIN sample_type st ON st.id = s.sample_type_id JOIN "
			+ "sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
			+ "sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
			+ "site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
			+ " JOIN region reg ON reg.id = d.region_id LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id "
			+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id LEFT JOIN "
			+ "lab destination_lab ON destination_lab.id = s.destination_lab_id where reg.id = :regionId AND (:status IS NULL OR ss2.id = :status) ", countQuery = "SELECT COUNT(s.id) FROM sample s "
					+ "JOIN sample_type st ON st.id = s.sample_type_id "
					+ "JOIN sample_status ss2 ON ss2.id = s.sample_status_id "
					+ "JOIN sample_retrieving sr ON s.sample_retrieving_id = sr.id "
					+ "JOIN site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
					+ " JOIN region reg ON reg.id = d.region_id "
					+ "LEFT JOIN sample_rejection sr2 ON sr2.sample_id = s.id "
					+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id "
					+ "LEFT JOIN lab destination_lab ON destination_lab.id = s.destination_lab_id where reg.id = :regionId AND (:status IS NULL OR ss2.id = :status)", nativeQuery = true)
	Page<Map<String, Object>> getSampleListByRegion(Pageable pageable, Integer regionId, Integer status);

	@Query(value = "SELECT s.id,reg.name AS region, d.name AS district, site.name AS site, "
			+ "st.name AS sample_type, s.sample_identifier, s.patient_identifier, "
			+ "s.collection_date, destination_lab.lab_name AS destination_lab, "
			+ "ss2.description status, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
			+ "COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) AS reception_date, s.lab_number, "
			+ "(s.collection_end_mileage - s.collection_start_mileage) distance, "
			+ "analysis_completed_date, analysis_released_date, result_collection_date, result_delivery_date, "
			+ "DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat, "
			+ "sr2.comment rejection_comment  FROM sample s JOIN sample_type st ON st.id = s.sample_type_id JOIN "
			+ "sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
			+ "sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
			+ "site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
			+ " JOIN region reg ON reg.id = d.region_id LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id "
			+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id LEFT JOIN "
			+ "lab destination_lab ON destination_lab.id = s.destination_lab_id where d.id = :districtId AND (:status IS NULL OR ss2.id = :status)", countQuery = "SELECT COUNT(s.id) FROM sample s "
					+ "JOIN sample_type st ON st.id = s.sample_type_id "
					+ "JOIN sample_status ss2 ON ss2.id = s.sample_status_id "
					+ "JOIN sample_retrieving sr ON s.sample_retrieving_id = sr.id "
					+ "JOIN site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
					+ " JOIN region reg ON reg.id = d.region_id "
					+ "LEFT JOIN sample_rejection sr2 ON sr2.sample_id = s.id "
					+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id "
					+ "LEFT JOIN lab destination_lab ON destination_lab.id = s.destination_lab_id where d.id = :districtId AND (:status IS NULL OR ss2.id = :status)", nativeQuery = true)
	Page<Map<String, Object>> getSampleListByDistrict(Pageable pageable, Integer districtId, Integer status);

	@Query(value = "SELECT s.id,reg.name AS region, d.name AS district, site.name AS site, "
			+ "st.name AS sample_type, s.sample_identifier, s.patient_identifier, "
			+ "s.collection_date, destination_lab.lab_name AS destination_lab, "
			+ "ss2.description status, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
			+ "COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) AS reception_date, s.lab_number, "
			+ "(s.collection_end_mileage - s.collection_start_mileage) distance, "
			+ "analysis_completed_date, analysis_released_date, result_collection_date, result_delivery_date, "
			+ "DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat, "
			+ "sr2.comment rejection_comment  FROM sample s JOIN sample_type st ON st.id = s.sample_type_id JOIN "
			+ "sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
			+ "sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
			+ "site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
			+ " JOIN region reg ON reg.id = d.region_id LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id "
			+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id LEFT JOIN "
			+ "lab destination_lab ON destination_lab.id = s.destination_lab_id where site.id = :siteId AND (:status IS NULL OR ss2.id = :status) ", countQuery = "SELECT COUNT(s.id) FROM sample s "
					+ "JOIN sample_type st ON st.id = s.sample_type_id "
					+ "JOIN sample_status ss2 ON ss2.id = s.sample_status_id "
					+ "JOIN sample_retrieving sr ON s.sample_retrieving_id = sr.id "
					+ "JOIN site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
					+ " JOIN region reg ON reg.id = d.region_id "
					+ "LEFT JOIN sample_rejection sr2 ON sr2.sample_id = s.id "
					+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id "
					+ "LEFT JOIN lab destination_lab ON destination_lab.id = s.destination_lab_id where site.id = :siteId AND (:status IS NULL OR ss2.id = :status)", nativeQuery = true)
	Page<Map<String, Object>> getSampleListBySite(Pageable pageable, Integer siteId, Integer status);

	@Query(value = "SELECT s.id,reg.name AS region, d.name AS district, site.name AS site, "
			+ "st.name AS sample_type, s.sample_identifier, s.patient_identifier, "
			+ "s.collection_date, destination_lab.lab_name AS destination_lab, "
			+ "ss2.description status, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
			+ "COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) AS reception_date, s.lab_number, "
			+ "(s.collection_end_mileage - s.collection_start_mileage) distance, "
			+ "analysis_completed_date, analysis_released_date, result_collection_date, result_delivery_date, "
			+ "DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat, "
			+ "sr2.comment rejection_comment  FROM sample s JOIN sample_type st ON st.id = s.sample_type_id JOIN "
			+ "sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
			+ "sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
			+ "site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
			+ " JOIN region reg ON reg.id = d.region_id LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id "
			+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id LEFT JOIN "
			+ "lab destination_lab ON destination_lab.id = s.destination_lab_id where s.collection_date between :startDate and :endDate  AND (:status IS NULL OR ss2.id = :status)", countQuery = "SELECT COUNT(s.id) FROM sample s "
					+ "JOIN sample_type st ON st.id = s.sample_type_id "
					+ "JOIN sample_status ss2 ON ss2.id = s.sample_status_id "
					+ "JOIN sample_retrieving sr ON s.sample_retrieving_id = sr.id "
					+ "JOIN site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
					+ " JOIN region reg ON reg.id = d.region_id "
					+ "LEFT JOIN sample_rejection sr2 ON sr2.sample_id = s.id "
					+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id "
					+ "LEFT JOIN lab destination_lab ON destination_lab.id = s.destination_lab_id where s.collection_date between :startDate and :endDate AND (:status IS NULL OR ss2.id = :status)", nativeQuery = true)
	Page<Map<String, Object>> getSampleListByDate(Pageable pageable, Date startDate, Date endDate, Integer status);

	@Query(value = "SELECT s.id,reg.name AS region, d.name AS district, site.name AS site, "
			+ "st.name AS sample_type, s.sample_identifier, s.patient_identifier, "
			+ "s.collection_date, destination_lab.lab_name AS destination_lab, "
			+ "ss2.description status, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
			+ "COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) AS reception_date, s.lab_number, "
			+ "(s.collection_end_mileage - s.collection_start_mileage) distance, "
			+ "analysis_completed_date, analysis_released_date, result_collection_date, result_delivery_date, "
			+ "DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat, "
			+ "sr2.comment rejection_comment  FROM sample s JOIN sample_type st ON st.id = s.sample_type_id JOIN "
			+ "sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
			+ "sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
			+ "site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
			+ " JOIN region reg ON reg.id = d.region_id LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id "
			+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id LEFT JOIN "
			+ "lab destination_lab ON destination_lab.id = s.destination_lab_id where reg.id = :regionId and s.collection_date between :startDate and :endDate AND (:status IS NULL OR ss2.id = :status) ", countQuery = "SELECT COUNT(s.id) FROM sample s "
					+ "JOIN sample_type st ON st.id = s.sample_type_id "
					+ "JOIN sample_status ss2 ON ss2.id = s.sample_status_id "
					+ "JOIN sample_retrieving sr ON s.sample_retrieving_id = sr.id "
					+ "JOIN site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
					+ " JOIN region reg ON reg.id = d.region_id "
					+ "LEFT JOIN sample_rejection sr2 ON sr2.sample_id = s.id "
					+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id "
					+ "LEFT JOIN lab destination_lab ON destination_lab.id = s.destination_lab_id where reg.id = :regionId and s.collection_date between :startDate and :endDate AND (:status IS NULL OR ss2.id = :status)", nativeQuery = true)
	Page<Map<String, Object>> getSampleListByDateAndRegion(Pageable pageable, Date startDate, Date endDate,
			Integer regionId, Integer status);

	@Query(value = "SELECT s.id,reg.name AS region, d.name AS district, site.name AS site, "
			+ "st.name AS sample_type, s.sample_identifier, s.patient_identifier, "
			+ "s.collection_date, destination_lab.lab_name AS destination_lab, "
			+ "ss2.description status, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
			+ "COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) AS reception_date, s.lab_number, "
			+ "(s.collection_end_mileage - s.collection_start_mileage) distance, "
			+ "analysis_completed_date, analysis_released_date, result_collection_date, result_delivery_date, "
			+ "DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat, "
			+ "sr2.comment rejection_comment  FROM sample s JOIN sample_type st ON st.id = s.sample_type_id JOIN "
			+ "sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
			+ "sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
			+ "site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
			+ " JOIN region reg ON reg.id = d.region_id LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id "
			+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id LEFT JOIN "
			+ "lab destination_lab ON destination_lab.id = s.destination_lab_id where d.id = :districtId and s.collection_date between :startDate and :endDate AND (:status IS NULL OR ss2.id = :status) ", countQuery = "SELECT COUNT(s.id) FROM sample s "
					+ "JOIN sample_type st ON st.id = s.sample_type_id "
					+ "JOIN sample_status ss2 ON ss2.id = s.sample_status_id "
					+ "JOIN sample_retrieving sr ON s.sample_retrieving_id = sr.id "
					+ "JOIN site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
					+ " JOIN region reg ON reg.id = d.region_id "
					+ "LEFT JOIN sample_rejection sr2 ON sr2.sample_id = s.id "
					+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id "
					+ "LEFT JOIN lab destination_lab ON destination_lab.id = s.destination_lab_id where d.id = :districtId and s.collection_date between :startDate and :endDate AND (:status IS NULL OR ss2.id = :status)", nativeQuery = true)
	Page<Map<String, Object>> getSampleListByDateAndDistrict(Pageable pageable, Date startDate, Date endDate,
			Integer districtId, Integer status);

	@Query(value = "SELECT s.id,reg.name AS region, d.name AS district, site.name AS site, "
			+ "st.name AS sample_type, s.sample_identifier, s.patient_identifier, "
			+ "s.collection_date, destination_lab.lab_name AS destination_lab, "
			+ "ss2.description status, COALESCE(lab.lab_name, hub.lab_name) AS lab, "
			+ "COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date) AS reception_date, s.lab_number, "
			+ "(s.collection_end_mileage - s.collection_start_mileage) distance, "
			+ "analysis_completed_date, analysis_released_date, result_collection_date, result_delivery_date, "
			+ "DATE_PART('DAY', COALESCE(s.deliver_at_lab_date, s.deliver_at_hub_date, now()) - s.collection_date) AS tat, "
			+ "sr2.comment rejection_comment  FROM sample s JOIN sample_type st ON st.id = s.sample_type_id JOIN "
			+ "sample_status ss2 ON ss2.id = s.sample_status_id JOIN "
			+ "sample_retrieving sr ON s.sample_retrieving_id = sr.id JOIN "
			+ "site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
			+ " JOIN region reg ON reg.id = d.region_id LEFT JOIN sample_rejection sr2 on sr2.sample_id = s.id "
			+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id LEFT JOIN "
			+ "lab destination_lab ON destination_lab.id = s.destination_lab_id where site.id = :siteId and s.collection_date between :startDate and :endDate AND (:status IS NULL OR ss2.id = :status) ", countQuery = "SELECT COUNT(s.id) FROM sample s "
					+ "JOIN sample_type st ON st.id = s.sample_type_id "
					+ "JOIN sample_status ss2 ON ss2.id = s.sample_status_id "
					+ "JOIN sample_retrieving sr ON s.sample_retrieving_id = sr.id "
					+ "JOIN site ON sr.site_id = site.id JOIN district d ON d.id = site.district_id "
					+ " JOIN region reg ON reg.id = d.region_id "
					+ "LEFT JOIN sample_rejection sr2 ON sr2.sample_id = s.id "
					+ "LEFT JOIN lab ON lab.id = s.reference_lab_id LEFT JOIN lab hub ON hub.id = s.hub_id "
					+ "LEFT JOIN lab destination_lab ON destination_lab.id = s.destination_lab_id where site.id = :siteId and s.collection_date between :startDate and :endDate AND (:status IS NULL OR ss2.id = :status)", nativeQuery = true)
	Page<Map<String, Object>> getSampleListByDateAndSite(Pageable pageable, Date startDate, Date endDate,
			Integer siteId, Integer status);

	// public List<Map<String, Object>> getSampleList();
}
