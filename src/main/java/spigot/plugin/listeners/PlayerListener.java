package spigot.plugin.listeners;

import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import spigot.plugin.TimeIsMoney;
import spigot.plugin.dao.UserDAO;
import spigot.plugin.model.User;
import spigot.plugin.utils.MessageTranslator;

import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

@NoArgsConstructor
public class PlayerListener implements Listener {
    private static final Logger logger = TimeIsMoney.getInstance().getLogger();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UserDAO userDAO = UserDAO.getInstance();
        MessageTranslator messageTranslator = MessageTranslator.getInstance();
        Optional<User> userOptional = userDAO.getByUsername(event.getPlayer().getDisplayName());
        if(userOptional.isEmpty()) {
            User user = new User( null, event.getPlayer().getDisplayName(), 10, new Date());
            user = userDAO.save(user);
            if(user != null) {
                logger.info("Salvataggio player eseguito con successo!");
                event.getPlayer().sendMessage(String.format(messageTranslator.getTranslation("new-player"), user.getUsername(), user.getMoney()));
            } else {
                logger.severe(String.format("Errore nel salvataggio dei dati per il player %s", event.getPlayer().getDisplayName()));
                event.getPlayer().sendMessage(messageTranslator.getTranslation("player-saving-data-error"));
            }
        } else {
            User user = userOptional.get();
            user.setLastRequestDate(new Date());
            userDAO.update(user);
            event.getPlayer().sendMessage(String.format(messageTranslator.getTranslation("welcome-back-player"), event.getPlayer().getDisplayName(), user.getMoney()));
        }
    }
}
