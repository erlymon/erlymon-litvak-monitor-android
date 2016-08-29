package org.erlymon.litvak.core.model.data;

/**
 * Created by sergey on 8/29/16.
 */
public class CommandResult {
    private boolean success;
    private String reason;

    public boolean isSuccess() {
        return success;
    }

    public String getReason() {
        return reason;
    }
}
