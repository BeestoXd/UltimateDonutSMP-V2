package com.bx.ultimateDonutSmp2.api;

import com.bx.ultimateDonutSmp2.UltimateDonutSmp2;
import org.jetbrains.annotations.NotNull;

public class UdsExpansion extends EconomyExpansion {

    public UdsExpansion(UltimateDonutSmp2 plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "uds";
    }
}
