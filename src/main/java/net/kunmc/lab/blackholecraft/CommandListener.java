package net.kunmc.lab.blackholecraft;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener implements TabExecutor {
    private final List<String> speeds = new ArrayList<>();
    public CommandListener() {
        Bukkit.getPluginCommand("blackHole").setExecutor(this);
        Bukkit.getPluginCommand("blackHole").setExecutor(this);
        for(double i = 0.1; i < 5; i += 0.1) {
            String speed = String.valueOf(i);
            if(speed.length() >= 3) {
                speed = speed.substring(0,3);
            }
            speeds.add(speed);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!command.getName().equals("blackHole")) {
            return true;
        }
        if(args.length < 1) {
            sendErrorMessage(sender,1);
            return true;
        }
        if(args[0].equals("on")) {
            BlackHoleCraft.getInstance().setEnable(true);
            sendSuccessMessage(sender,"plugin", "有効");
            return true;
        }
        if(args[0].equals("off")) {
            BlackHoleCraft.getInstance().setEnable(false);
            sendSuccessMessage(sender,"plugin","無効");
            return true;
        }
        if(args[0].equals("show")) {
            sender.sendMessage("§a" + "player: " + BlackHoleCraft.getInstance().getPlayerName());
            sender.sendMessage("§a" + "speed: " + BlackHoleCraft.getInstance().getSpeed());
            sender.sendMessage("§a" + "isIncludePlayer: " + BlackHoleCraft.getInstance().isIncludePlayer());
            sender.sendMessage("§a" + "isEnable: " + BlackHoleCraft.getInstance().isEnable());
            return true;
        }
        if(args[0].equals("set")) {
            if(args.length < 3) {
                sendErrorMessage(sender,2);
                return true;
            }
            if(args[1].equals("player")) {
                Player player = BlackHoleCraft.getInstance().getServer().getPlayer(args[2]);
                if(player == null) {
                    sendErrorMessage(sender,3);
                    return true;
                }
                BlackHoleCraft.getInstance().setBlackHole(player.getUniqueId());
                sendSuccessMessage(sender,"player", args[2]);
                return true;
            }
            if(args[1].equals("speed")) {
                try {
                    double speed = Double.parseDouble(args[2]);
                    if(speed <= 0 || 5 < speed) {
                        sendErrorMessage(sender,4);
                        return true;
                    }
                    BlackHoleCraft.getInstance().setSpeed(speed);
                    sendSuccessMessage(sender,"speed", args[2]);
                    return true;
                } catch (Exception e) {
                    sendErrorMessage(sender,4);
                    return true;
                }
            }
            if(args[1].equals("isIncludePlayer")) {
                if(!args[2].equals("true") && !args[2].equals("false")) {
                    sendErrorMessage(sender,5);
                    return true;
                }
                boolean isIncludePlayer = Boolean.parseBoolean(args[2]);
                BlackHoleCraft.getInstance().setIncludePlayer(isIncludePlayer);
                sendSuccessMessage(sender,"isIncludePlayer", args[2]);
                return true;
            }
        }
        sendErrorMessage(sender,1);
        return true;
    }

    private void sendSuccessMessage(CommandSender sender, String item, String content) {
        sender.sendMessage("§a" + item + "を" + content + "にしました");
    }

    private void sendErrorMessage(CommandSender sender, int errorType) {
        switch (errorType) {
            case 1:
                sender.sendMessage("§c" + "set" + " player " + "playerName");
                sender.sendMessage("§c" + "set" + " speed " + "(0<n<5)");
                sender.sendMessage("§c" + "set" + " isIncludePlayer " + "true or false");
                sender.sendMessage("§c" + "show");
                sender.sendMessage("§c" + "on");
                sender.sendMessage("§c" + "off");
                break;
            case 2:
                sender.sendMessage("§c" + "引数が足りません");
                sender.sendMessage("§c" + "set" + " player " + "playerName");
                sender.sendMessage("§c" + "set" + " speed " + "(0<n<5)");
                sender.sendMessage("§c" + "set" + " isIncludePlayer " + "true or false");
                break;
            case 3:
                sender.sendMessage("§c" + "存在しないプレイヤーです");
                break;
            case 4:
                sender.sendMessage("§c" + "有効な数値(0<n<5)を入力してください");
                break;
            case 5:
                sender.sendMessage("§c" + "true または false を入力してください");
                break;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("blackHole")) {
            if(args.length == 1) {
                return Stream.of("set","on", "off", "show").filter(e -> e.startsWith(args[0])).collect(Collectors.toList());
            }
            if(args.length == 2 && args[0].equals("set")) {
                return Stream.of("player","speed", "isIncludePlayer").filter(e -> e.startsWith(args[1])).collect(Collectors.toList());
            }
            if(args.length == 3 && args[1].equals("player")) {
                return BlackHoleCraft.getInstance().getServer().getOnlinePlayers().stream().map(HumanEntity::getName)
                        .filter(e -> e.startsWith(args[2])).collect(Collectors.toList());
            }
            if(args.length == 3 && args[1].equals("speed")) {
                return speeds.stream().filter(e -> e.startsWith(args[2])).collect(Collectors.toList());
            }
            if(args.length == 3 && args[1].equals("isIncludePlayer")) {
                return Stream.of("true","false").filter(e -> e.startsWith(args[2])).collect(Collectors.toList());
            }
        }
        return null;
    }
}
