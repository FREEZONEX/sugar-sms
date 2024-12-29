package org.niiish32x.sugarsms.alarm.app.external;

/**
 * AlarmRequest
 *
 * Supos Alarms 请求参数
 * https://cloud.supos.com/open/APIs/detail?treeNodeCode=1392&openapiVersionCode=c3bc97b89d05458d8cef7ee0c810dd6b&apisManageCode=25353e4b1c614159ab4ffc3f9ddbb769&clickActive=supOS
 * @author shenghao ni
 * @date 2024.12.29 10:21
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRequest  {
    private String instanceEnName;
    private String instanceDisplayName;
    private List<String> instanceLabelNames;
    private String attributeEnName;
    private String attributeDisplayName;
    private String attributeComment;
    private List<String> attributeLabelNames;
    private String alarmEnName;
    private String alarmDisplayName;
    private String alarmComment;
    private Integer alarmPriority;
    private Boolean alarmEnabled;
    private String alarmType;
    private Integer page;
    private Integer perPage;
    private Boolean excludeFunctionSetAlarm;


}