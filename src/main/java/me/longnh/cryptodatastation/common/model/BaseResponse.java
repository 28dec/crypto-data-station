package me.longnh.cryptodatastation.common.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseResponse {
    public String status;
    public String errorCode;
    public String errorMessage;
    public Object data;
}
