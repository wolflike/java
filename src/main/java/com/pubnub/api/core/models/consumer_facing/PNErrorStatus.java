package com.pubnub.api.core.models.consumer_facing;

import lombok.Data;

@Data
public class PNErrorStatus extends PNStatus {

    PNErrorData errorData;

}
