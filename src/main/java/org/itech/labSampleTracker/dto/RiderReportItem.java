package org.itech.labSampleTracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RiderReportItem {
	//collected
	private Integer collectedBICount = 0;
	private Integer collectedBSCount = 0;
	private Integer collectedCVCount= 0;
	private Integer collectedEIDCount = 0;
	private Integer collectedTBCount = 0;
	private Integer collectedHPVCount = 0;
	//delivered
	private Integer deliveredBICount = 0;
	private Integer deliveredBSCount = 0;
	private Integer deliveredCVCount= 0;
	private Integer deliveredEIDCount = 0;
	private Integer deliveredHubCVCount= 0;
	private Integer deliveredHubEIDCount = 0;
	private Integer deliveredTBCount = 0;
	private Integer deliveredHPVCount = 0;
	//non-conform
	private Integer ncBICount = 0;
	private Integer ncBSCount = 0;
	private Integer ncCVCount= 0;
	private Integer ncEIDCount = 0;
	private Integer ncHubCVCount= 0;
	private Integer ncHubEIDCount = 0;
	private Integer ncTBCount = 0;
	private Integer ncHPVCount = 0;
	//collectedResult
	private Integer resultCollectedBICount = 0;
	private Integer resultCollectedBSCount = 0;
	private Integer resultCollectedCVCount= 0;
	private Integer resultCollectedEIDCount = 0;
	private Integer resultCollectedHubCVCount= 0;
	private Integer resultCollectedHubEIDCount = 0;
	private Integer resultCollectedTBCount = 0;
	private Integer resultCollectedHPVCount = 0;
	//delevered Result
	private Integer resultDeliveredBICount = 0;
	private Integer resultDeliveredBSCount = 0;
	private Integer resultDeliveredCVCount= 0;
	private Integer resultDeliveredEIDCount = 0;
	private Integer resultDeliveredHubCVCount= 0;
	private Integer resultDeliveredHubEIDCount = 0;
	private Integer resultDeliveredTBCount = 0;
	private Integer resultDeliveredHPVCount = 0;
	
}
