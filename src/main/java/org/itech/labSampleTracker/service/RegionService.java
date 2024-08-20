/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.Region;

import java.util.List;
import java.util.Map;

/**
 * <h2>RegionServiceimpl</h2>
 */
public interface RegionService {
	Region create(Region d);

	Region update(Region d);

	Region getOne(int id);

	List<Region> getAll();

	long getTotal();

	boolean delete(int id);

	List<Map<String, Object>> getRegionIdAndName();
}
