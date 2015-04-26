package pt.uptodate.handlers;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import pt.uptodate.UpToDate;

import java.io.File;


public class Config {
	public static Configuration configuration;

	public static boolean chat = false;

	public static void init(File configFile) {

		//create configuration object from the given file
		if (configuration == null)
		{
			configuration = new Configuration(configFile);
			loadConfiguration();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase(UpToDate.MOD_ID))
		{
			loadConfiguration();
		}
	}

	private static void loadConfiguration()
	{
		chat = configuration.getBoolean("chat", Configuration.CATEGORY_GENERAL, false, "Setting this to true will turn on chat update notifications");

		if (configuration.hasChanged())
		{
			configuration.save();
		}
	}
}