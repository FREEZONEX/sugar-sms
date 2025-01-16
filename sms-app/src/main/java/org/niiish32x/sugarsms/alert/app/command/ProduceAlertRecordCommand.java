package org.niiish32x.sugarsms.alert.app.command;

import lombok.Data;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;

/**
 * ProductAlertRecordCommand
 *
 * @author shenghao ni
 * @date 2025.01.02 15:32
 */

@Data
public class ProduceAlertRecordCommand {
    private final AlertInfoDTO alertInfoDTO;

    public ProduceAlertRecordCommand(AlertInfoDTO alertInfoDTO) {
        if (alertInfoDTO == null) {
            throw new IllegalArgumentException("alertInfoDTO cannot be null");
        }

        this.alertInfoDTO = alertInfoDTO;
    }
}
