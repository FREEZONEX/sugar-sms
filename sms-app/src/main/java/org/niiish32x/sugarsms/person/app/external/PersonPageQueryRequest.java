package org.niiish32x.sugarsms.person.app.external;

import com.google.common.base.Preconditions;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * SuposPersonQueryRequest
 *
 * @author shenghao ni
 * @date 2025.01.08 14:38
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonPageQueryRequest {

    // Accept-Language，国际化语言，默认为服务器操作系统语言
    private String acceptLanguage;
    // 翻页的页数，从1开始
    @Builder.Default
    private int pageNo = 1;
    // 每页返回的元素数量
    private int pageSize;
    // 公司编码，必填项
    @NonNull
    private String companyCode;
    // 部门编码
    private String departmentCode;
    // 岗位编码
    private String positionCode;
    // 查询的人员状态，有ALL、OFFLINE、ONLINE几种取值
    private String scope;

    private boolean systemDefault;
    // 是否绑定用户
    @Builder.Default
    private boolean hasBoundUser = false;
    // 根据用户名查询（hasBoundUser=true时生效）
    private String username;
    // 根据直属领导查询
    private String directLeaderCode;
    // 根据隔级领导查询
    private String grandLeaderCode;
    // 根据手机号查询
    private String phoneNumber;
    // 根据邮箱查询
    private String email;
    // 根据身份证号码查询
    private String idNumber;
    // 根据性别查询，根据系统编码取值
    private String gender;
    // 指定sort排序方式
    private String orderType;
    // 人员编号集合，查询指定的人员，单次请求最多20个编号，参数以“,”隔开
    private List<String> codes;

    @Builder.Default
    boolean getAll = false;

    public Map<String,String> buildQueryMap() {
        Preconditions.checkArgument(StringUtils.isNotBlank(this.companyCode),"companyCode 必须非空");

        Map<String, String> queryMap = new HashMap<>();
        addNonBlankField(queryMap, "companyCode", this::getCompanyCode);
        addNonBlankField(queryMap, "departmentCode", this::getDepartmentCode);
        addNonBlankField(queryMap, "positionCode", this::getPositionCode);
        addNonBlankField(queryMap,"hasBoundUser",() -> String.valueOf(this.hasBoundUser));
        addNonBlankField(queryMap,"username",this::getUsername);

        if(codes != null &&  !codes.isEmpty()) {
            Preconditions.checkArgument((codes.size() <= 20),"人员编号集合，查询指定的人员，单次请求最多20个编号");
            List<String> codes = this.codes;
            String codesParams = String.join(",", codes);
            queryMap.put("codes",codesParams);
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
