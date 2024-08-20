/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.Sample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <h2>SampleServiceimpl</h2>
 */
public interface SampleService {

	Sample create(Sample d);

	Sample update(Sample d);

	boolean removeById(Integer d);

	Sample getOne(int id);

	List<Sample> getAll();

	List<Map<String, String>> getAll(Integer region, Integer district, Integer site, Date startDate, Date endDate,
			Integer status);

	long getTotal();

	boolean delete(int id);

	Sample findBySampleIdentifier(String identifier);

	List<Map<String, Object>> getSampleInTransitForUser(Integer userId);

	List<Map<String, Object>> getSampleInTransitForUserAndLab(Integer userId, Integer labId);

	List<Map<String, Object>> getSampleInTransitForUserAndLab(Integer userId, Integer labId, List<String> sampleTypes);

	List<Map<String, Object>> getSampleAtHubForAcceptance(Integer hubId);

	List<Map<String, Object>> getAcceptedSampleAtHub(Integer hubId);

	List<Map<String, Object>> getSampleAtLabForAcceptance(Integer labId, String status);

	List<Map<String, Object>> getAcceptedSampleAtLab(Integer labId, String status);

	List<Map<String, Object>> getAnalysisDoneAtLab(Integer labId);

	List<Map<String, Object>> getSampleResultForUserAndSite(Integer userId, Integer labId);

	List<Map<String, Object>> getAnalysisFailedAtLab(Integer labId);

	List<Map<String, Object>> getPendingResultAtLab(Integer labId);

	Map<String, Object> getSampleCountByStatus();

	Map<String, Object> getSampleCountByStatus(Integer regionId, Integer districtId, Integer siteId, Date startDate,
			Date endDate);

	Map<String, Object> getSampleCountByStatus(Date startDate, Date endDate);

	Map<String, Object> getSampleCountByStatusForRegion(Integer regionId, Date startDate, Date endDate);

	Map<String, Object> getSampleCountByStatusForDistrict(Integer districtId, Date startDate, Date endDate);

	Map<String, Object> getSampleCountByStatusForSite(Integer siteId, Date startDate, Date endDate);

	List<Map<String, Object>> getSampleDetails();

	Page<Map<String, Object>> getSampleDetails(Pageable pageable, Integer regionId, Integer districtId, Integer siteId,
			Date startDate, Date endDate, Integer status);

	Map<String, Map<String, Integer>> getSampleStatusBySampleType();

	Map<String, Map<String, Integer>> getSampleStatusBySampleType(Integer regionId, Integer districtId, Integer siteId,
			Date startDate, Date endDate);

}
