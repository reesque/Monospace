package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.AppPackage;

import java.util.List;

public interface AppPackageSubscriber extends MonoSubscriber {
    void update(List<AppPackage> packages);
}
