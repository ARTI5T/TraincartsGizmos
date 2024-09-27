package work.art1st.traincartsgizmos;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import work.art1st.traincartsgizmos.utils.TrainUtil;

public class GizmosCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendPlainMessage("Wrong parameters");
            return false;
        }
        MinecartGroup group = TrainPropertiesStore.getRelaxed(args[1]).getHolder();
        switch (args[0]) {
            case "init" -> {
                if (args.length != 2) {
                    sender.sendPlainMessage("Wrong parameters");
                    return false;
                }
                TrainUtil.initialize(group);
                return true;
            }
            case "reverse" -> {
                if (args.length != 2) {
                    sender.sendPlainMessage("Wrong parameters");
                    return false;
                }
                TrainUtil.switchDirection(group);
                return true;
            }
            case "lightOn" -> {
                if (args.length != 2) {
                    sender.sendPlainMessage("Wrong parameters");
                    return false;
                }
                TrainUtil.turnLightsOn(group);
                return true;
            }
            case "lightOff" -> {
                if (args.length != 2) {
                    sender.sendPlainMessage("Wrong parameters");
                    return false;
                }
                TrainUtil.turnLightsOff(group);
                return true;
            }
            case "openDoor" -> {
                if (args.length != 4) {
                    sender.sendPlainMessage("Wrong parameters");
                    return false;
                }
                boolean openLeft = args[2].equals("l") || args[2].equals("lr");
                boolean openRight = args[2].equals("r") || args[2].equals("lr");
                TrainUtil.openDoor(group, openLeft, openRight, Double.parseDouble(args[3]));
                return true;
            }
            default -> sender.sendPlainMessage("Unknown command");
        }
        return false;
    }
}
