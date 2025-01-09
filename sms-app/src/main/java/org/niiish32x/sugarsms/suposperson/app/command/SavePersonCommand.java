package org.niiish32x.sugarsms.suposperson.app.command;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonDTO;

/**
 * SavePersonCommand
 *
 * @author shenghao ni
 * @date 2025.01.09 11:37
 */

@Getter
@Slf4j
public class SavePersonCommand {
    private final SuposPersonDTO suposPersonDTO;

    public SavePersonCommand(SuposPersonDTO suposPersonDTO) {
        Preconditions.checkArgument(suposPersonDTO != null, "suposPersonDTO must not be null");
        this.suposPersonDTO = suposPersonDTO;
    }

}
