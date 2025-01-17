package org.niiish32x.sugarsms.alert.app.command;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;

/**
 * SaveAlertCommand
 *
 * @author shenghao ni
 * @date 2025.01.17 10:41
 */

@Getter
public class SaveAlertCommand {
    private final AlertInfoDTO alertInfoDTO;

    public SaveAlertCommand(AlertInfoDTO alertInfoDTO) {
        Preconditions.checkArgument(alertInfoDTO != null, "alertInfoDTO must not be null");
        this.alertInfoDTO = alertInfoDTO;
    }
}
