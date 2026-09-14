package com.bx.ultimateDonutSmp2.api;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.jetbrains.annotations.NotNull;

public class UltimateDonutSmp2Expansion extends EconomyExpansion {

    public UltimateDonutSmp2Expansion(UltimateDonutSmp2 plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ultimatedonutsmp2";
    }
}
