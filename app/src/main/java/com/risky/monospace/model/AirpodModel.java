package com.risky.monospace.model;

public enum AirpodModel {
    MODEL_AIRPODS_GEN1("airpods1", "AirPod 1"),
    MODEL_AIRPODS_GEN2("airpods2", "AirPod 2"),
    MODEL_AIRPODS_GEN3("airpods3", "AirPod 3"),
    MODEL_AIRPODS_PRO("airpodspro", "AirPod Pro"),
    MODEL_AIRPODS_PRO_2("airpodspro2", "AirPod Pro 2"),
    MODEL_AIRPODS_MAX("airpodsmax", "AirPod Pro Max"),
    MODEL_BEATS_X("beatsx", "Beats X"),
    MODEL_BEATS_FLEX("beatsflex", "Beats Flex"),
    MODEL_BEATS_SOLO_3("beatssolo3", "Beats Solo 3"),
    MODEL_BEATS_STUDIO_3("beatsstudio3", "Beats Studio 3"),
    MODEL_POWERBEATS_3("powerbeats3", "Power Beats 3"),
    MODEL_POWERBEATS_PRO("powerbeatspro", "Power Beats Pro"),
    MODEL_UNKNOWN("unknown", "Unknown");
    
    public final String name;
    public final String displayName;

    AirpodModel(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public static AirpodModel get(String name) {
        for (AirpodModel model : values()) {
            if (model.name.equals(name)) {
                return model;
            }
        }

        return MODEL_UNKNOWN;
    }
}
