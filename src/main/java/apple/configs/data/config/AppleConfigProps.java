package apple.configs.data.config;

public class AppleConfigProps {
    private static final AppleConfigProps EMPTY = new AppleConfigProps(new String[0]);
    private String[] path;

    public AppleConfigProps(String[] path) {
        this.path = path;
    }

    public AppleConfigProps(AppleConfigProps other) {
        this.path = other.path;
    }

    public static AppleConfigProps empty() {
        return EMPTY;
    }

    public AppleConfigProps addOuterProps(AppleConfigProps outerProps) {
        return this.copy()
                .folderize(outerProps.path());
    }

    private AppleConfigProps folderize(String[] outerPath) {
        String[] newPath = new String[outerPath.length + this.path.length];
        System.arraycopy(outerPath, 0, newPath, 0, outerPath.length);
        System.arraycopy(this.path, 0, newPath, outerPath.length, this.path.length);
        this.path = newPath;
        return this;
    }

    private AppleConfigProps copy() {
        return new AppleConfigProps(this);
    }

    public String[] path() {
        return this.path;
    }
}
