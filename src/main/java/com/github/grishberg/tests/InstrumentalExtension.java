package com.github.grishberg.tests;

/**
 * Extension for sending arguments to plugin.
 */
public class InstrumentalExtension {
    String flavorName;
    String androidSdkPath;
    String applicationId;
    String instrumentalPackage;
    String instrumentalRunner;
    String instrumentListener = "com.github.grishberg.annotationprinter.AnnotationsTestPrinter";
    boolean coverageEnabled;
    boolean makeScreenshotsWhenFail;
    boolean saveLogcat;
    boolean shardEnabled;

    public InstrumentalExtension() { /* default constructor */ }

    /**
     * Copy constructor.
     */
    public InstrumentalExtension(InstrumentalExtension src) {
        this.flavorName = src.flavorName;
        this.androidSdkPath = src.androidSdkPath;
        this.applicationId = src.applicationId;
        this.instrumentalPackage = src.instrumentalPackage;
        this.instrumentalRunner = src.instrumentalRunner;
        this.instrumentListener = src.instrumentListener;
        this.coverageEnabled = src.coverageEnabled;
        this.makeScreenshotsWhenFail = src.makeScreenshotsWhenFail;
        this.saveLogcat = src.saveLogcat;
        this.shardEnabled = src.shardEnabled;
    }

    public void setFlavorName(String flavorName) {
        this.flavorName = flavorName;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public String getAndroidSdkPath() {
        return androidSdkPath;
    }

    public void setAndroidSdkPath(String androidSdkPath) {
        this.androidSdkPath = androidSdkPath;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getInstrumentalPackage() {
        return instrumentalPackage;
    }

    public void setInstrumentalPackage(String instrumentalPackage) {
        this.instrumentalPackage = instrumentalPackage;
    }

    public String getInstrumentalRunner() {
        return instrumentalRunner;
    }

    public void setInstrumentalRunner(String instrumentalRunner) {
        this.instrumentalRunner = instrumentalRunner;
    }

    public boolean isCoverageEnabled() {
        return coverageEnabled;
    }

    public void setCoverageEnabled(boolean coverageEnabled) {
        this.coverageEnabled = coverageEnabled;
    }

    public String getInstrumentListener() {
        return instrumentListener;
    }

    public void setInstrumentListener(String instrumentListener) {
        this.instrumentListener = instrumentListener;
    }

    public boolean isMakeScreenshotsWhenFail() {
        return makeScreenshotsWhenFail;
    }

    public void setMakeScreenshotsWhenFail(boolean makeScreenshotsWhenFail) {
        this.makeScreenshotsWhenFail = makeScreenshotsWhenFail;
    }

    public boolean isSaveLogcat() {
        return saveLogcat;
    }

    public void setSaveLogcat(boolean saveLogcat) {
        this.saveLogcat = saveLogcat;
    }

    /**
     * @return true when need to enable sharding for all available devices.
     */
    public boolean isShardEnabled() {
        return shardEnabled;
    }

    public void setShardEnabled(boolean shardEnabled) {
        this.shardEnabled = shardEnabled;
    }
}
