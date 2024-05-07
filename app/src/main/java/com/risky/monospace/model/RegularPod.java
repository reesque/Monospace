package com.risky.monospace.model;

public class RegularPod extends Pod {
    public final String leftStatus;
    public final String rightStatus;
    public final String caseStatus;
    public final boolean leftCharging;
    public final boolean rightCharging;
    public final boolean caseCharging;
    public final boolean leftInEar;
    public final boolean rightInEar;

    public RegularPod(String model, boolean isDisconnected, String leftStatus, String rightStatus,
                      String caseStatus, boolean leftCharging, boolean rightCharging,
                      boolean caseCharging, boolean leftInEar, boolean rightInEar) {
        super(model, isDisconnected, false);

        this.leftStatus = leftStatus;
        this.rightStatus = rightStatus;
        this.caseStatus = caseStatus;
        this.leftCharging = leftCharging;
        this.rightCharging = rightCharging;
        this.caseCharging = caseCharging;
        this.leftInEar = leftInEar;
        this.rightInEar = rightInEar;
    }
}
