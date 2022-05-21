package apple.configs.data.config;

import java.util.ArrayList;
import java.util.List;

public record ApplePartialConfigPath(List<String> path) {
    @Override
    public int hashCode() {
        long hashCode = 0;
        for (String p : path) {
            hashCode += p.hashCode();
        }
        return (int) (hashCode % Integer.MAX_VALUE);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ApplePartialConfigPath other)) return false;
        if (this.path.size() != other.path.size()) return false;
        for (int size = path.size(), i = 0; i < size; i++) {
            if (!this.path.get(i).equals(other.path.get(i))) return false;
        }
        return true;
    }

    public ApplePartialConfigPath addCopy(String add) {
        List<String> newPath = new ArrayList<>(this.path);
        newPath.add(add);
        return new ApplePartialConfigPath(newPath);
    }
}
