package com.ricky.apicommon.userInfo.VO;

import java.util.List;
import java.util.Set;

public record KeysValuesRecord(
        Set<String> keys,
        List<String> values
) {
}
