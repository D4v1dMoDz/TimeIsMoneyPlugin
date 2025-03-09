package spigot.plugin.commands;

import lombok.NoArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import spigot.plugin.TimeIsMoney;
import spigot.plugin.dao.UserDAO;
import spigot.plugin.model.User;
import spigot.plugin.utils.MessageTranslator;

import java.util.Date;
import java.util.Optional;

@NoArgsConstructor
public class TimeIsMoneyCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        MessageTranslator messageTranslator = MessageTranslator.getInstance();
        int moneyPerMinute = TimeIsMoney.getInstance().getCustomConfig().getInt("earnings.money.minute");
        if(moneyPerMinute != 0 && commandSender instanceof Player player) {
            Optional<User> userOptional = UserDAO.getInstance().getByUsername(player.getDisplayName());
            if(userOptional.isPresent()) {
                User user = userOptional.get();
                long elapsedTime = getTimeElapsedFromDate(user.getLastRequestDate());
                if(elapsedTime >= 1L) {
                    int earnedMoney = (int) (moneyPerMinute * elapsedTime);
                    user.setLastRequestDate(new Date());
                    user.setMoney(user.getMoney() + earnedMoney);
                    UserDAO.getInstance().update(user);
                    player.sendMessage(String.format(messageTranslator.getTranslation("withdrawal"), earnedMoney, user.getMoney()));
                } else {
                    player.sendMessage(messageTranslator.getTranslation("withdrawal-time-not-reached"));
                }
            }
        }
        return true;
    }

    private long getTimeElapsedFromDate(Date date) {
        Date timeElapsed = new Date(new Date().getTime() - date.getTime());
        return timeElapsed.getTime()/60000;
    }
}
