package work.art1st.traincartsgizmos.utils;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.wrappers.Brightness;
import com.bergerkiller.bukkit.tc.attachments.VirtualArmorStandItemEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualDisplayItemEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentLight;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

public class AttachmentUtil {
    public static void forEachTaggedAttachment(MinecartMember<?> minecart, String tag, BiConsumer<Attachment, ConfigurationNode> consumer) {
        minecart.getAttachments().getAllAttachments().stream()
                .filter(attachment -> attachment != null && attachment.getConfig().contains("extra." + tag))
                .forEach(attachment -> consumer.accept(attachment, attachment.getConfig().getNode("extra." + tag)));
    }

    public static void forEachTaggedAttachment(MinecartGroup group, String tag, BiConsumer<Attachment, ConfigurationNode> consumer) {
        group.forEach(cart -> forEachTaggedAttachment(cart, tag, consumer));
    }

    public static boolean changeItem(CartAttachmentItem attachment, @Nullable ItemStack item, @Nullable Brightness brightness) {
        VirtualSpawnableObject virtualEntity = getVirtualSpawnableObject(attachment);
        if (item != null && virtualEntity != null) {
            if (virtualEntity instanceof VirtualDisplayItemEntity itemEntity) {
                itemEntity.setItem(itemEntity.getMode(), item);
                if (brightness != null) {
                    itemEntity.setBrightness(brightness);
                }
            } else if (virtualEntity instanceof VirtualArmorStandItemEntity itemEntity) {
                itemEntity.setItem(itemEntity.getTransformType(), item);
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean changeLightLevel(CartAttachmentLight attachment, int level) {
        if (level < 0 || level > 15) {
            return false;
        }
        ConfigurationNode config = attachment.getConfig().clone();
        config.set("level", level);
        attachment.onLoad(config);
        return true;
    }

    public static @Nullable VirtualSpawnableObject getVirtualSpawnableObject(CartAttachmentItem attachment) {
        try {
            Field entityField = CartAttachmentItem.class.getDeclaredField("entity");
            entityField.setAccessible(true);
            return (VirtualSpawnableObject) entityField.get(attachment);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
