package work.art1st.traincartsgizmos.utils;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import work.art1st.traincartsgizmos.TrainCartsGizmos;

import java.util.List;
import java.util.function.Consumer;

import static java.lang.Math.max;

public class DoorUtil {
    private static void schedule(List<String> ops, long startTick, Consumer<String> task) {
        long tick = startTick;
        for (String s : ops) {
            String[] split = s.split(" ");
            Bukkit.getScheduler().runTaskLater(TrainCartsGizmos.getInstance(), () -> task.accept(split[0]), tick);
            if (split.length == 2) {
                tick += (long) (Double.parseDouble(split[1]) * 20.0);
            }
        }
    }
    static void openDoor(MinecartGroup group, boolean left, boolean right, double duration) {
        long durationTick = (long) (duration * 20);
        //Bukkit.getLogger().info("extra" + group.getProperties().getConfig().contains("extra.doorAnimation.close.time"));
        long doorCloseTick = (long) (group.getProperties().getConfig().get("extra.doorAnimation.close.time", Double.class) * 20);
        long closesAt = max(durationTick - doorCloseTick, 0);
        boolean reversed = group.getProperties().getConfig().getOrDefault("extra.heading.reversed", false);
        TriConsumer<String, String, Long> t = (op, side, startTick) -> {
            schedule(group.getProperties().getConfig().getList("extra.doorAnimation." + op + "." + side + "Light", String.class), startTick, state -> {
                AttachmentUtil.forEachTaggedAttachment(group, "light", (attachment, config) -> {
                    LightUtil.setState(attachment, config, state);
                });
            });
            schedule(group.getProperties().getConfig().getList("extra.doorAnimation." + op + "." + side, String.class), startTick, anim -> {
                String[] split = anim.split("@");
                double speed = split.length == 2 ? Double.parseDouble(split[1]) : 1.0;
                String[] split2 = split[0].split("\\[");
                String animName = split2[0];
                String scene = split2.length == 2 ? split2[1].substring(0, split2[1].length() - 1) : null;
                AnimationOptions options = new AnimationOptions();
                options.setName(animName);
                options.setSpeed(speed);
                options.setReset(true);
                //Bukkit.getLogger().info(animName + " " + scene + " " + speed);
                if (scene != null) {
                    String[] split3 = scene.split(":");
                    if (split3.length == 2) {
                        options.setScene(split3[0], split3[1]);
                    } else {
                        options.setScene(scene);
                    }
                }
                group.playNamedAnimation(options);
            });
        };
        if ((left && !reversed) || (right && reversed)) {
            t.accept("open", "left", 0L);
            t.accept("close", "left", closesAt);
        }
        if ((right && !reversed) || (left && reversed)) {
            t.accept("open", "right", 0L);
            t.accept("close", "right", closesAt);
        }
    }
}
