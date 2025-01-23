package org.niiish32x.sugarsms.user.app.external;

import com.google.common.base.Preconditions;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.common.enums.CompanyEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * UserPageQueryRequest
 *
 *
 *  Supos User 请求参数
 *
 *  https://cloud.supos.com/open/APIs/detail?treeNodeCode=1681&openapiVersionCode=c3bc97b89d05458d8cef7ee0c810dd6b&apisManageCode=25353e4b1c614159ab4ffc3f9ddbb769&clickActive=supOS
 *
 * @author shenghao ni
 * @date 2025.01.05 16:38
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageQueryRequest {
    private String keyword;
    @Builder.Default
    private Integer pageIndex = 1;
    @Builder.Default
    private Integer pageSize = 10;

    private String roleCode;

    @NonNull
    @Builder.Default
    private String companyCode = CompanyEnum.DEFAULT.value;

    @Builder.Default
    private boolean includeDeleted = false;

    /**
     * 该参数为true 则表示全量查询
     */
    @Builder.Default
    private boolean getAll = false;

    public UserPageQueryRequest(String keyword,Integer pageIndex,Integer pageSize,String roleCode,String companyCode,boolean getAll) {
        this.keyword = keyword;
        this.pageIndex =  pageIndex;
        this.pageSize =  pageSize;
        this.roleCode = roleCode;
        this.companyCode = companyCode;
        Preconditions.checkArgument(companyCode != null, "company cannot be null");
        this.includeDeleted = false;
        this.getAll = getAll;
    }

    public Map<String, String> buildQueryMap() {

        Preconditions.checkArgument(StringUtils.isNotBlank(companyCode),"company 不能为空");

        Map<String, String> queryMap = new HashMap<>();
        addNonBlankField(queryMap, "keyword", this::getKeyword);
        addNonBlankField(queryMap, "pageIndex", () -> String.valueOf(this.pageIndex));
        addNonBlankField(queryMap, "pageSize", () -> String.valueOf(this.pageSize));
        addNonBlankField(queryMap, "companyCode", this::getCompanyCode);
        addNonBlankField(queryMap, "roleCode", this::getRoleCode);
        addNonBlankField(queryMap, "includeDeleted", () -> String.valueOf(this.includeDeleted));

        return queryMap;
    }


    private void addNonBlankField(Map<String, String> queryMap, String key, Supplier<String> supplier) {
        String value = supplier.get();
        if (StringUtils.isNotBlank(value)) {
            queryMap.put(key, value);
        }
    }
}
