package work.art1st.traincartsgizmos;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TrainCartsGizmos extends JavaPlugin {
    @Getter
    private static TrainCartsGizmos instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        Objects.requireNonNull(Bukkit.getPluginCommand("tcg")).setExecutor(new GizmosCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
