package com.template.dto;

import java.time.LocalDate;

//Class made for future addition in response

public class LogisticStateResponseDto extends Farm_LogisticFlowDto {

    public LogisticStateResponseDto(String logisticId, String waybillNo, LocalDate pickupDate, String pickupLocation, Double tempInTransit, LocalDate dropDate, String dropLocation, String batchNo, String carrierName) {
        super(logisticId, waybillNo, pickupDate, pickupLocation, tempInTransit, dropDate, dropLocation, batchNo, carrierName);
    }

    public LogisticStateResponseDto(){}
}
