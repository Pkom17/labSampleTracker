/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.District;

import java.util.List;
import java.util.Map;

/**
 * <h2>DistrictServiceimpl</h2>
 */
public interface DistrictService {
	District create(District d);

	District update(District d);

	District getOne(int id);

	List<District> getAll();

	long getTotal();

	boolean delete(int id);

	public List<Map<String, Object>> getDistrictIdAndNames();

	public List<Map<String, Object>> getDistrictIdAndNamesByRegion(Integer regionId);
	
	public List<Map<String, Object>> getDistrictIdAndNamesByRegions(List<Integer> regions);
}
