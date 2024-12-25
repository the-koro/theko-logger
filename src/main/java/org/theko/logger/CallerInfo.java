package org.theko.logger;

public class CallerInfo {
    private final String className;
    private final String methodName;
    private final boolean isNativeMethod;
    private final String moduleName;
    private final String moduleVersion;
    private final String classLoaderName;
    private final String threadName;
    private final String fileName;
    private final int lineNumber;

    // Constructor with all String parameters
    public CallerInfo(String className, String methodName, boolean isNativeMethod, String moduleName, 
                      String moduleVersion, String classLoaderName, String threadName, String fileName, int lineNumber) {
        this.className = className;
        this.methodName = methodName;
        this.isNativeMethod = isNativeMethod;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.classLoaderName = classLoaderName;
        this.threadName = threadName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    // Constructor with StackTraceElement and thread name
    public CallerInfo(StackTraceElement stackTraceElement, String threadName) {
        this.className = stackTraceElement.getClassName();
        this.methodName = stackTraceElement.getMethodName();
        this.isNativeMethod = stackTraceElement.isNativeMethod();
        this.moduleName = stackTraceElement.getModuleName();
        this.moduleVersion = stackTraceElement.getModuleVersion();
        this.classLoaderName = stackTraceElement.getClassLoaderName();
        this.threadName = threadName;
        this.fileName = stackTraceElement.getFileName();
        this.lineNumber = stackTraceElement.getLineNumber();
    }

    // Getters for all fields (optional, depending on your needs)
    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isNativeMethod() {
        return isNativeMethod;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public String getClassLoaderName() {
        return classLoaderName;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "CallerInfo{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", moduleVersion='" + moduleVersion + '\'' +
                ", classLoaderName='" + classLoaderName + '\'' +
                ", threadName='" + threadName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", lineNumber='" + lineNumber + '\'' +
                '}';
    }
}
