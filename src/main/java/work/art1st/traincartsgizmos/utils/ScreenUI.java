package work.art1st.traincartsgizmos.utils;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import work.art1st.traincartsgizmos.TrainCartsGizmos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenUI {
    protected static class Operation {
        @Getter
        private final ItemStack item;
        @Getter
        private final long tick;
        public Operation(String command) {
            String[] split = command.split(" ");
            String[] item = split[0].split(":");
            this.item = new ItemStack(Material.valueOf(item[0]));
            if (item.length > 1) {
                ItemMeta itemMeta = this.item.getItemMeta();
                itemMeta.setCustomModelData(Integer.parseInt(item[1]));
                this.item.setItemMeta(itemMeta);
            }
            if (split.length > 1) {
                this.tick = Integer.parseInt(split[1]);
            } else {
                this.tick = 0;
            }
        }
    }
    protected static class OperationPerScreen {
        private final List<Operation> operationList;
        private final String screenName;
        public OperationPerScreen(String name, List<String> commands) {
            this.screenName = name;
            this.operationList = new ArrayList<>();
            for (String command : commands) {
                this.operationList.add(new Operation(command));
            }
        }
        private static void applyToDisplay(MinecartMember<?> minecart, String tag, ItemStack item) {
            //Bukkit.getLogger().info("Changing " + tag + " item to " + item);
            AttachmentUtil.forEachTaggedAttachment(minecart, "display", (attachment, config) -> {
                if (attachment instanceof CartAttachmentItem itemAttachment && tag.equals(config.get("type", String.class))) {
                    AttachmentUtil.changeItem(itemAttachment, item, null);
                }
            });
        }
        public void apply(MinecartMember<?> minecart) {
            long tick = 0;
            //Bukkit.getLogger().info("Applying screen " + screenName + " to " + minecart.getEntity().getUniqueId());
            for (Operation operation : operationList) {
                Bukkit.getScheduler().runTaskLater(TrainCartsGizmos.getInstance(), () -> {
                    applyToDisplay(minecart, screenName, operation.getItem());
                }, tick);
                tick += operation.getTick();
            }
        }
    }
    protected final Map<String, OperationPerScreen> screenOperations;
    public ScreenUI(ConfigurationNode config) {
        //Bukkit.getLogger().info("Loading screens " + config.getName());
        this.screenOperations = new HashMap<>();
        config.getKeys().forEach(key -> {
            //Bukkit.getLogger().info("Loading screen " + key + config.getList(key, String.class).size());
            screenOperations.put(key, new OperationPerScreen(config.getName(), config.getList(key, String.class)));
        });
    }

    public boolean applyTo(MinecartMember<?> minecart, String name) {
        OperationPerScreen ops = screenOperations.get(name);
        if (ops == null) {
            //Bukkit.getLogger().info("Failed to apply screen " + name);
            return false;
        }
        //Bukkit.getLogger().info("Applying screen " + name);
        ops.apply(minecart);
        return true;
    }

    public void applyTo(MinecartGroup group, String name) {
        group.forEach(cart -> applyTo(cart, name));
    }
}
