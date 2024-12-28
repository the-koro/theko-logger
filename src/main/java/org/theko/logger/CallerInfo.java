package org.theko.logger;

/**
 * Holds detailed information about the caller of the log entry, including the class, 
 * method, file, and other relevant metadata.
 */
public class CallerInfo {
    private final String className;  // Class name from which the log was called
    private final String methodName;  // Method name from which the log was called
    private final boolean isNativeMethod;  // Whether the method is native (true if native)
    private final String moduleName;  // Module name, if available
    private final String moduleVersion;  // Module version, if available
    private final String classLoaderName;  // Class loader name, if available
    private final String threadName;  // Name of the thread from which the log was called
    private final String fileName;  // File name from which the log was called
    private final int lineNumber;  // Line number from which the log was called

    /**
     * Constructor that initializes all fields with the provided values.
     * 
     * @param className       The class name from which the log was called.
     * @param methodName      The method name from which the log was called.
     * @param isNativeMethod  Whether the method is native (true if native).
     * @param moduleName      The module name, if available.
     * @param moduleVersion   The module version, if available.
     * @param classLoaderName The class loader name, if available.
     * @param threadName      The thread name from which the log was called.
     * @param fileName        The file name from which the log was called.
     * @param lineNumber      The line number from which the log was called.
     */
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

    /**
     * Constructor that initializes the caller info from a StackTraceElement and thread name.
     * 
     * @param stackTraceElement The stack trace element from which to extract the caller information.
     * @param threadName        The thread name from which the log was called.
     */
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

    // Getter methods for retrieving caller details

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

    /**
     * Returns a string representation of the CallerInfo.
     * 
     * @return A formatted string representation of the caller information.
     */
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
