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
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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




    public Map<String, String> buildQueryMap() {
        Map<String, String> queryMap = new HashMap<>();

        addNonBlankField(queryMap, "instanceEnName", this::getInstanceEnName);
        addNonBlankField(queryMap, "instanceDisplayName", this::getInstanceDisplayName);
        addNonBlankField(queryMap, "attributeEnName", this::getAttributeEnName);
        addNonBlankField(queryMap, "attributeDisplayName", this::getAttributeDisplayName);
        addNonBlankField(queryMap, "attributeComment", this::getAttributeComment);
        addNonBlankField(queryMap, "alarmEnName", this::getAlarmEnName);
        addNonBlankField(queryMap, "alarmDisplayName", this::getAlarmDisplayName);
        addNonBlankField(queryMap, "alarmComment", this::getAlarmComment);
        addNonBlankField(queryMap, "alarmType", this::getAlarmType);

        // 处理 alarmPriority
        Integer alarmPriority = this.alarmPriority;
        if (alarmPriority != null && alarmPriority > 0 && alarmPriority <= 10) {
            queryMap.put("alarmPriority", alarmPriority.toString());
        }

        // 处理 page 和 perPage
        Integer page = this.page;
        if (page != null && page > 0) {
            queryMap.put("page", page.toString());
        }

        Integer perPage = this.perPage;
        if (perPage != null && perPage > 0 && perPage <= 500) {
            queryMap.put("perPage", perPage.toString());
        }

        return queryMap;
    }

    private void addNonBlankField(Map<String, String> queryMap, String key, Supplier<String> supplier) {
        String value = supplier.get();
        if (StringUtils.isNotBlank(value)) {
            queryMap.put(key, value);
        }
    }


}