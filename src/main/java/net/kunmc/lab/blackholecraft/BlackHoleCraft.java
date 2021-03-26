package net.kunmc.lab.blackholecraft;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.UUID;

public final class BlackHoleCraft extends JavaPlugin {
    private static BlackHoleCraft INSTANCE;

    public static BlackHoleCraft getInstance() {
        return INSTANCE;
    }

    private UUID blackHoleId;
    private boolean isEnable = true;
    private double speed = 0.1;
    private boolean isIncludePlayer = false;

    public void setBlackHole(UUID blackHoleId) {
        this.blackHoleId = blackHoleId;
    }

    public String getPlayerName() {
        if(blackHoleId == null) {
            return "empty";
        }
        return getServer().getPlayer(blackHoleId).getName();
    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setIncludePlayer(boolean isIncludePlayer) {
        this.isIncludePlayer = isIncludePlayer;
    }

    public boolean isIncludePlayer() {
        return isIncludePlayer;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        blackHoleTask();
        new CommandListener();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void blackHoleTask() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (!isEnable || blackHoleId == null) {
                    return;
                }
                Player blackHole = getServer().getPlayer(blackHoleId);
                if(!isValid(blackHole)) {
                    return;
                }
                blackHole.getWorld().getEntities().forEach(entity -> {
                    if(entity instanceof Player) {
                        if(!isIncludePlayer || entity.getUniqueId().equals(blackHoleId)) {
                            return;
                        }
                    }
                    if(isValid(blackHole)) {
                        pullEntity(blackHole, entity);
                    }
                });
            }
        },0L,2L);
    }

    private boolean isValid(Player player) {
        return player != null && player.isValid() && !player.isDead() && player.isOnline();
    }

    private void pullEntity(Player puller, Entity entity) {
        if(speed <= 0) {
            return;
        }
        Vector pv = puller.getLocation().toVector();
        Vector ev = entity.getLocation().toVector();
        try {
            pv.checkFinite();
            ev.checkFinite();
        } catch (IllegalArgumentException e) {
            return;
        }
        Vector velocity = pv.subtract(ev).normalize().multiply(speed);
        try {
            velocity.checkFinite();
        } catch (IllegalArgumentException e) {
            return;
        }
        entity.setVelocity(entity.getVelocity().add(velocity));
    }
}
