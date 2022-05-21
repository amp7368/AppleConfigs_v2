package apple.configs.data.config;


public record AppleConfigPath(String name, String[] path) {

    @Override
    public int hashCode() {
        int hashCode = this.name.hashCode();
        for (String p : path) hashCode += p.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AppleConfigPath other) {
            if (!this.name.equals(other.name)) return false;
            if (this.path.length != other.path.length) return false;
            for (int i = 0; i < path.length; i++) {
                if (!this.path[i].equals(other.path[i])) return false;
            }
            return true;
        }
        return false;
    }
}
