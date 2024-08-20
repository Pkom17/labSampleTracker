package org.itech.labSampleTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SampleCollectedDTO {
	private Integer id;
	private Integer userId;
	private Integer sampleId;
	private Integer circuitId;
	private Integer sampleRetrievingId;
	private Integer rejectionTypeId;
	private String rejectionComment;
	private Integer destinationLabId;
	private Integer siteId;
	private Integer hubId; // final hub Id
	private Integer labId;//final lab Id
	//private String siteCode;
	private String patientIdentifier;
	private String sampleIdentifier;
	private String circuitNumber;
	private Integer collectionStartMileage;
	private Integer collectionEndMileage;
	private Integer resultStartMileage;
	private Integer resultEndMileage;
	private String collectionDate;
	private String deliveredAtHubDate;
	private String deliveredAtReferenceLabDate;
	private String acceptedAtHubDate;
	private String acceptedAtReferenceLabDate;
	private String rejectionDate;
	private String sampleType;
	private String labNumber;
	private String analysisCompletedDate;
	private String analysisReleasedDate;
	private String analysisResultReportedDate;
	private String requesterSiteName;
	private String destinationLabName;
	private String resultCollectionDate;
	private String resultDeliveryDate;
	private String status;
	private String action;
}
