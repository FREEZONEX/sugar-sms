package org.niiish32x.sugarsms.app.proxy;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * AlertConcentBuilder
 *
 * @author shenghao ni
 * @date 2025.01.17 11:41
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AlertContentBuilder {

    String sourcePropertyName;
    String newValue;
    String source;
    Long startDataTimestamp;

    String limitValue;
}
