package work.art1st.traincartsgizmos.utils;

import com.bergerkiller.bukkit.common.wrappers.Brightness;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentLight;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class LightUtil {
    static void turnOn(Attachment attachment, ConfigurationNode config) {
        setState(attachment, config, "toggledOn");
    }

    static void turnOff(Attachment attachment, ConfigurationNode config) {
        setState(attachment, config, "toggledOff");
    }

    static void setState(Attachment attachment, ConfigurationNode config, String state) {
        if (attachment instanceof CartAttachmentItem itemAttachment) {
            //Bukkit.getLogger().info(config.getPath() + ": " + state);
            if (config.contains(state)) {
                //Bukkit.getLogger().info("Changing item to " + config.get(state + ".item"));
                AttachmentUtil.changeItem(
                        itemAttachment,
                        config.get(state + ".item", ItemStack.class),
                        Brightness.blockAndSkyLight(config.getOrDefault(state + ".brightness.block", 15), config.get(state + ".brightness.sky", 0))
                );
                //AttachmentUtil.changeDisplayItemBrightness(itemAttachment, Brightness.blockAndSkyLight(config.getOrDefault(state + ".brightness.block", 15), config.get(state + ".brightness.sky", 0)));
            }
        } else if (attachment instanceof CartAttachmentLight lightAttachment) {
            AttachmentUtil.changeLightLevel(lightAttachment, config.get(state + ".lightLevel", 15));
        }
    }
}
