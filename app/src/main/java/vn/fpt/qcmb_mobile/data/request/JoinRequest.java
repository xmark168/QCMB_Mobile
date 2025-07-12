package vn.fpt.qcmb_mobile.data.request;

import java.util.UUID;

public class JoinRequest {
    private UUID match_id;

    public UUID getMatch_id() {
        return match_id;
    }

    public void setMatch_id(UUID match_id) {
        this.match_id = match_id;
    }
}
