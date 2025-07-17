package vn.fpt.qcmb_mobile.data.api;

import java.util.Map;

    public interface OnEventListener {
        void onEvent(String type, Map<String, Object> data);
    }

