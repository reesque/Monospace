package com.risky.monospace.util;

import com.risky.monospace.model.AppPackage;

import java.util.List;

public interface PacManSubscriber {
    void update(List<AppPackage> packages);
}
