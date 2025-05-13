package com.itsschatten.yggdrasil;

import com.itsschatten.yggdrasil.commands.YggdrasilTestCommand;
import com.itsschatten.yggdrasil.menus.MenuUtils;
import com.itsschatten.yggdrasil.wands.WandUtils;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class YggdrasilPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Utils.setInstance(this);
        Utils.setDebug(true);
        MenuUtils.initialize(this);
        WandUtils.initalize(this);

        // Plugin startup logic
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            final Commands registrar = event.registrar();
            new YggdrasilTestCommand().register(registrar);
        });
    }

    @Override
    public void onDisable() {
    }
}
