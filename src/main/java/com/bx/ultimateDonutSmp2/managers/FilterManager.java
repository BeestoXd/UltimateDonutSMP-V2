package com.bx.ultimateDonutSmp2.managers;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class FilterManager {

    private final UltimateDonutSmp2 plugin;
    private final LinkedHashMap<String, Set<Material>> categories = new LinkedHashMap<>();

    public FilterManager(UltimateDonutSmp2 plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        categories.clear();
        FileConfiguration config = plugin.getConfigManager().getFilter();
        if (config == null) return;

        for (String key : config.getKeys(false)) {
            List<String> matNames = config.getStringList(key);
            if (matNames == null) continue;
            Set<Material> materials = new LinkedHashSet<>();
            for (String name : matNames) {
                Material mat = Material.matchMaterial(name);
                if (mat != null) {
                    materials.add(mat);
                }
            }
            categories.put(key, materials);
        }
    }

    public List<String> categoryNames() {
        return new ArrayList<>(categories.keySet());
    }

    public Set<Material> resolve(String category) {
        if (category == null) return Collections.emptySet();
        return categories.getOrDefault(category, Collections.emptySet());
    }
}
