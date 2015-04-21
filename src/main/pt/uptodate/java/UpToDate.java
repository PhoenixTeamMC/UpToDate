package main.uptodate.java;

import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import api.java.IUpdateable;
import main.uptodate.java.util.Logger;
import main.uptodate.java.util.ReflectionUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod(modid = UpToDate.MOD_ID, version = UpToDate.VERSION, name = UpToDate.MOD_NAME)
public class UpToDate implements IUpdateable
{
    public static final String MOD_ID = "uptodate";
	public static final String MOD_NAME = "UpToDate";
    public static final String VERSION = "1.0";
	public static final int SIMPLE_VERSION = 1;

	public ArrayList<FetchedUpdateable> updates = new ArrayList<FetchedUpdateable>();

	@EventHandler
	public void complete(FMLLoadCompleteEvent event) {
		if (netIsAvailable()) {
			Object objMods = ReflectionUtil.getFieldValFromObj(Loader.instance(), "mods");
			Object objController = ReflectionUtil.getFieldValFromObj(Loader.instance(), "modController");

			if (objController instanceof LoadController && objMods instanceof List) {
				List mods = (List) objMods;
				LoadController controller = (LoadController) objController;
				Logger.info("Got mods list");
				for (Object mod : mods) {
					if (mod instanceof ModContainer && ((ModContainer) mod).getMod() instanceof IUpdateable) {
						IUpdateable modObj = (IUpdateable) ((ModContainer) mod).getMod();
						Logger.info("Checking if " + modObj.getName() + " is out of date");
						FetchedUpdateable toBe = new FetchedUpdateable(modObj);
						if (toBe.diff > 0) {
							updates.add(toBe);
						}
					}
				}
			}

			Logger.info("The following mods are out of date: " + StringUtils.join(updates, ','));
		}
	}

	@Override
	public String getName() {
		return MOD_NAME;
	}

	@Override
	public String getRemote() {
		try {
			URL updateUrl = new URL("http://raw.githubusercontent.com/PhoenixTeamMC/UpToDate/master/version/version.yaml");
			InputStreamReader r = new InputStreamReader(updateUrl.openStream());
			return IOUtils.toString(r);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public HashMap<String, String> getLocal() {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("technical", String.valueOf(SIMPLE_VERSION));
		result.put("display", VERSION);
		return result;
	}

	private static boolean netIsAvailable() {
		try {
			final URL url = new URL("http://www.google.com");
			final URLConnection conn = url.openConnection();
			conn.connect();
			return true;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			return false;
		}
	}
}