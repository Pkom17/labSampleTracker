/*
 * Java domain class for entity "Region" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import org.itech.labSampleTracker.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>RegionRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "Region" Repository
 */


public interface RegionRepository  extends JpaRepository<Region, Integer> , JpaSpecificationExecutor<Region> {

}
