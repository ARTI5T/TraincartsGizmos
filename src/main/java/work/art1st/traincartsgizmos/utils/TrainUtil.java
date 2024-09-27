package work.art1st.traincartsgizmos.utils;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import org.jetbrains.annotations.Nullable;

public final class TrainUtil {
    public static void initialize(MinecartGroup group) {
        int headCart = group.getProperties().getConfig().getOrDefault("extra.headCart", 1);
        /* TODO: 获取头车/尾车位置 */
        AttachmentUtil.forEachTaggedAttachment(group.head().getNeighbour(headCart), "light", (attachment, config) -> {
            LightUtil.setState(attachment, config, "head");
        });
        AttachmentUtil.forEachTaggedAttachment(group.tail().getNeighbour(-headCart), "light", (attachment, config) -> {
            LightUtil.setState(attachment, config, "tail");
        });
        turnLightsOn(group);
        if (!group.getProperties().getConfig().contains("extra.heading.reversed")) {
            group.getProperties().getConfig().set("extra.heading.reversed", false);
            AttachmentUtil.forEachTaggedAttachment(group, "display", (attachment, config) -> {
                if (config.contains("reversed")) {
                    Boolean attachmentReversed = config.get("reversed", Boolean.class);
                    attachment.setActive(!attachmentReversed);
                }
            });
        }
    }
    public static void switchDirection(MinecartGroup group) {
        boolean reversed = group.getProperties().getConfig().getOrDefault("extra.heading.reversed", false);
        group.getProperties().getConfig().set("extra.heading.reversed", !reversed);
        int headCart = group.getProperties().getConfig().getOrDefault("extra.headCart", 1);
        AttachmentUtil.forEachTaggedAttachment(group.head().getNeighbour(headCart), "light", (attachment, config) -> {
            LightUtil.setState(attachment, config, "tail");
        });
        AttachmentUtil.forEachTaggedAttachment(group.tail().getNeighbour(-headCart), "light", (attachment, config) -> {
            LightUtil.setState(attachment, config, "head");
        });
        AttachmentUtil.forEachTaggedAttachment(group, "display", (attachment, config) -> {
            if (config.contains("reversed")) {
                Boolean attachmentReversed = config.get("reversed", Boolean.class);
                if (attachmentReversed) {
                    attachment.setActive(reversed);
                } else {
                    attachment.setActive(!reversed);
                }
            }
        });
        group.reverse();
    }
    public static void turnLightsOn(MinecartGroup group) {
        AttachmentUtil.forEachTaggedAttachment(group, "light", LightUtil::turnOn);
    }
    public static void turnLightsOff(MinecartGroup group) {
        AttachmentUtil.forEachTaggedAttachment(group, "light", LightUtil::turnOff);
    }
    public static void openDoor(MinecartGroup group, boolean left, boolean right, double duration) {
        DoorUtil.openDoor(group, left, right, duration);
    }
    public static @Nullable Integer getCarNumber(MinecartMember<?> member) {
        return member.getProperties().getConfig().get("extra.carNum", Integer.class);
    }
    public static String getTrainType(MinecartMember<?> member) {
        return getTrainType(member.getGroup());
    }
    public static String getTrainType(MinecartGroup group) {
        return group.getProperties().getConfig().get("extra.type", String.class);
    }
}
