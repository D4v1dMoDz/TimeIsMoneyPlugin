package spigot.plugin.logger.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;

public class Log4jFilter extends AbstractFilter {
    public static void registerFilter()
    {
        Logger logger = (Logger)LogManager.getRootLogger();
        logger.addFilter(new Log4jFilter());
    }

    @Override
    public Result filter(LogEvent event)
    {
        if(event == null)
        {
            return Result.NEUTRAL;
        }
        if(event.getLoggerName().contains("Hikari"))
        {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }
}
