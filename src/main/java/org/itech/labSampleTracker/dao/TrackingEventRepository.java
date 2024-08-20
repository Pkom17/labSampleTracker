/*
 * Java domain class for entity "TrackingEvent" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import org.itech.labSampleTracker.entities.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>TrackingEventRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "TrackingEvent" Repository
 */


public interface TrackingEventRepository  extends JpaRepository<TrackingEvent, Integer> , JpaSpecificationExecutor<TrackingEvent> {

}
