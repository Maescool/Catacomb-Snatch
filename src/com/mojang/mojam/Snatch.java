package com.mojang.mojam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.network.Packet;

public final class Snatch
{
	private static boolean init = false;
	public static File modDir;
	public static List<Mod> modList = new ArrayList<Mod>();
	public static List<ScriptEngine> scriptList = new ArrayList<ScriptEngine>();
	private static MojamComponent mojam;
	public static Map<Integer, Entity> spawnList = new HashMap<Integer, Entity>();
	private static ScriptEngineManager lang = new ScriptEngineManager();

	public static void init(MojamComponent m)
	{
		init = true;
		mojam = m;
		try
		{
			modDir = new File(Snatch.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		}
		catch (URISyntaxException e1)
		{
			e1.printStackTrace();
		}
		/*try
		{
			System.setOut(new PrintStream(new FileOutputStream(new File(m.getMojamDir(), "log.txt"))));
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}*/

		System.out.println("Snatch starting up...");
		addMod(Snatch.class.getClassLoader(), "SnatchContent.class");
		try
		{
			System.out.println(modDir.getAbsolutePath());
			readLinksFromFile(new File(mojam.getMojamDir(), "mods.txt"));
			readFromClassPath(new File(mojam.getMojamDir(), "/mods"));
			readFromClassPath(modDir);
			//readLinksFromFile(new File(mojam.getMojamDir(),"mods.txt"));
			System.out.println(modDir.getAbsolutePath());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		for(Mod mod : modList)
		{
			//System.out.println(mod.getClass());
		}
	}

	private static void readFromModFolder(File file) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException
	{
		ClassLoader classloader = (com.mojang.mojam.MojamComponent.class).getClassLoader();
		Method method = (java.net.URLClassLoader.class).getDeclaredMethod("addURL", new Class[]
		{
			java.net.URL.class
		});
		method.setAccessible(true);
		if(!file.isDirectory())
		{
			throw new IllegalArgumentException("Folder must be a Directory.");
		}
		File afile[] = file.listFiles();
		Arrays.sort(afile);
		if(classloader instanceof URLClassLoader)
		{
			for(int i = 0; i < afile.length; i++)
			{
				File file1 = afile[i];
				if(file1.isDirectory() || file1.isFile() && (file1.getName().endsWith(".jar") || file1.getName().endsWith(".zip")))
				{
					method.invoke(classloader, new Object[]
					{
						file1.toURI().toURL()
					});
				}
			}
		}
		for(int j = 0; j < afile.length; j++)
		{
			File file2 = afile[j];
			if(file2.isDirectory() || file2.isFile() && (file2.getName().endsWith(".jar") || file2.getName().endsWith(".zip")))
			{
				if(file2.isFile())
				{
					FileInputStream fileinputstream = new FileInputStream(file2);
					ZipInputStream zipinputstream = new ZipInputStream(fileinputstream);
					Object obj = null;
					do
					{
						ZipEntry zipentry = zipinputstream.getNextEntry();
						if(zipentry == null)
						{
							break;
						}
						String s1 = zipentry.getName();
						if(!zipentry.isDirectory() && s1.startsWith("mod_") && s1.endsWith(".class"))
						{
							addMod(classloader, s1);
						}
					}
					while(true);
					zipinputstream.close();
					fileinputstream.close();
				}
				else if(file2.isDirectory())
				{
					Package package1 = (com.mojang.mojam.MojamComponent.class).getPackage();
					if(package1 != null)
					{
						String s = package1.getName().replace('.', File.separatorChar);
						file2 = new File(file2, s);
					}
					File afile1[] = file2.listFiles();
					if(afile1 != null)
					{
						for(int k = 0; k < afile1.length; k++)
						{
							String s2 = afile1[k].getName();
							if(afile1[k].isFile() && s2.startsWith("mod_") && s2.endsWith(".class"))
							{
								addMod(classloader, s2);
							}
						}
					}
				}
			}
		}
	}

	public static MojamComponent getMojam()
	{
		return mojam;
	}

	private static Mod addMod(ClassLoader classloader, String s)
	{
		try
		{
			String s1 = s.split("\\.")[0];
			Package package1 = (com.mojang.mojam.Snatch.class).getPackage();
			if(package1 != null)
			{
				s1 = (new StringBuilder(String.valueOf(package1.getName()))).append(".").append(s1.substring(s.lastIndexOf('/') + 1)).toString();
			}
			Class class1 = classloader.loadClass(s1);
			if(!(Mod.class).isAssignableFrom(class1))
			{
				return null;
			}
			Mod mod = (Mod) class1.newInstance();
			if(mod != null)
			{
				modList.add(mod);
				System.out.println("Mod Initialized: " + mod.getClass().getSimpleName());
				return mod;
			}
		}
		catch (Exception throwable)
		{
			throwable.printStackTrace();
			System.out.println((new StringBuilder("Failed to load mod from \"")).append(s).append("\"").toString());
		}
		return null;
	}

	private static void readFromClassPath(File file) throws FileNotFoundException, IOException
	{
		ClassLoader classloader = (Snatch.class).getClassLoader();
		if(file.isFile() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")))
		{
			System.out.println("Reading from classpath " + file.getAbsolutePath());
			FileInputStream fileinputstream = new FileInputStream(file);
			ZipInputStream zipinputstream = new ZipInputStream(fileinputstream);
			Object obj = null;
			do
			{
				ZipEntry zipentry = zipinputstream.getNextEntry();
				if(zipentry == null)
				{
					break;
				}
				String s1 = zipentry.getName();
				if(!zipentry.isDirectory() && s1.contains("mod_") && s1.endsWith(".class"))
				{
					//System.out.println(s1);//TODO
					addMod(classloader, s1);
				}
				else if(!zipentry.isDirectory() && s1.contains("mod_"))
				{
					//System.out.println(s1);//TODO
					addScript(s1);
				}
			}
			while(true);
			fileinputstream.close();
		}
		else if(file.isDirectory())
		{
			try
			{
				readFromModFolder(file);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			Package package1 = (Snatch.class).getPackage();
			if(package1 != null)
			{
				String s = package1.getName().replace('.', File.separatorChar);
				file = new File(file, s);
			}
			File afile[] = file.listFiles();
			if(afile != null)
			{
				for(int i = 0; i < afile.length; i++)
				{
					String s2 = afile[i].getName();
					if(afile[i].isFile() && s2.startsWith("mod_") && s2.endsWith(".class"))
					{
						addMod(classloader, s2);
					}
					else if(afile[i].isFile() && s2.contains("mod_"))
					{
						//System.out.println(s2);//TODO
						addScript(afile[i].getAbsolutePath());
					}
				}
			}
		}
	}

	public static ScriptEngine addScript(String s)
	{
		ScriptEngine e = lang.getEngineByExtension(s.substring(s.lastIndexOf('.') + 1));
		/*for(ScriptEngineFactory factory:lang.getEngineFactories())
		{
			System.out.println(factory.getLanguageName()+":"+factory.getEngineName());
			for(String s1:factory.getExtensions())
			{
				System.out.println("|__> "+ s1);
			}
		}*/
		try
		{
			FileReader fr = new FileReader(s);
			e.eval(fr);
			e.put("Snatch", new Snatch());
			scriptList.add(e);
			System.out.println(e.getFactory().getExtensions().get(0).toUpperCase() + " Script initialised: " + s);
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch (NullPointerException e1)
		{
			System.out.println("Could not initialise mod " + s);
		}
		catch (ScriptException e1)
		{
			e1.printStackTrace();
		}
		return e;
	}

	@Deprecated
	public static void addJSMod(ClassLoader c, String s)
	{
		ScriptEngine e = lang.getEngineByExtension("js");
		try
		{
			FileReader fr = new FileReader(s);
			e.eval(fr);
			scriptList.add(e);
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch (ScriptException e1)
		{
			e1.printStackTrace();
		}
	}

	public static Entity getEntityById(int i, double x, double y)
	{
		for(Mod m : modList)
		{
			Entity e = m.getEntityInstanceById(i, x, y);
			if(e != null) return e;
		}
		return null;
	}

	public static int numEntitiesLoaded()
	{
		return spawnList.size();
	}

	public static int addEntity(Entity entity)
	{
		spawnList.put(spawnList.size(), entity);
		int i = spawnList.size() - 1;
		System.out.println("Registered " + spawnList.get(i).getClass().getSimpleName() + " with id " + i);
		return i;
	}

	public static void afterRender()
	{
		for(Mod m : modList)
		{
			m.OnRender();
		}
		invoke("OnRender");
	}

	public static void startRender()
	{
		for(Mod m : modList)
		{
			m.OnStartRender();
		}
		invoke("OnStartRender");
	}

	public static void afterTick()
	{
		for(Mod m : modList)
		{
			m.AfterTick();
		}
		invoke("AfterTick");
	}

	public static void runOnce()
	{
		for(Mod m : modList)
		{
			m.RunOnce();
		}
		invoke("RunOnce");
	}

	public static void createLevel(Level level)
	{
		for(Mod m : modList)
		{
			m.CreateLevel(level);
		}
		invoke("CreateLevel");
	}

	public static void onStop()
	{
		for(Mod m : modList)
		{
			m.OnClose();
		}
		invoke("OnStop");
	}

	public static void onWin(int i)
	{
		for(Mod m : modList)
		{
			m.OnVictory(i);
		}
		invoke("OnVictory", i);
	}

	public static void levelTick(Level level)
	{
		for(Mod m : modList)
		{
			m.OnLevelTick(level);
		}
		invoke("OnLevelTick", level);
	}

	public static void updateTick()
	{
		for(Mod m : modList)
		{
			m.OnTick();
		}
		invoke("OnTick");
	}

	public static void sendPacket(Packet packet)
	{
		for(Mod m : modList)
		{
			m.OnSendPacket(packet);
		}
		invoke("OnSendPacket", packet);
	}

	public static void receivePacket(Packet packet)
	{
		for(Mod m : modList)
		{
			m.OnReceivePacket(packet);
		}
		invoke("OnReceivePacket", packet);
	}

	public static void handlePacket(Packet packet)
	{
		for(Mod m : modList)
		{
			m.HandlePacket(packet);
		}
		invoke("HandlePacket", packet);
	}

	public static void invoke(String s, Object... args)
	{
		for(ScriptEngine sc : scriptList)
		{
			Invocable i = (Invocable) sc;
			if(args.length > 0)
			{
				for(Object o : args)
				{
					sc.put(o.getClass().getSimpleName(), o);
				}
			}
			try
			{
				i.invokeFunction(s, args);
			}
			catch (NoSuchMethodException e)
			{
				System.out.println("Bad method name: " + s);
			}
			catch (ScriptException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static long currentTimeMillis()
	{
		return System.currentTimeMillis();
	}

	public static long nanoTime()
	{
		return System.nanoTime();
	}

	public static Font getFont()
	{
		return Font.getFont();
	}

	public static void setGamemode(GameMode gamemode)
	{
		TitleMenu.defaultGameMode = gamemode;
	}

	public static void readLinksFromFile(File f) throws IOException
	{
		if(!f.exists())
		{
			System.out.println("Creating Mod Subscriptions File");
			f.createNewFile();
		}

		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		while((line = reader.readLine()) != null)
		{
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		line = stringBuilder.toString();

		String[] links = line.split("\n|\r");
		List<String> stringList = new LinkedList();
		for(String s : links)
		{
			File f1 = new File(mojam.getMojamDir(), "/mods/" + s.substring(s.lastIndexOf('/') + 1));
			if(!f1.exists())f1.createNewFile();
			try
			{
				if(!upToDate(s))
				{
					System.out.println("Debug: " + s);//TODO
					File f2 = downloadFile(s, f1.getAbsolutePath());
					stringList.add(f2.getAbsolutePath());
					System.out.println(f2.getAbsolutePath());
				}
			}
			catch (Exception e)
			{

			}
		}
		for(String s : stringList)
		{
			System.out.println(addScript(s));
		}
	}

	public static boolean upToDate(String s) throws IOException
	{
		File f = new File(s);
		File f1 = new File(mojam.getMojamDir(), "mods/" + s.substring(s.lastIndexOf('/') + 1));
		if(!f1.exists()) f1.mkdirs();
		f1.createNewFile();
		System.out.println(f.hashCode() + ":" + f1.hashCode() + " - " + f.lastModified() + ":" + f1.lastModified());
		if(f.hashCode() != f1.hashCode() && f.lastModified() > f1.lastModified())
		{
			System.out.println(f.hashCode() + ":" + f1.hashCode() + " - " + f.lastModified() + ":" + f1.lastModified());
			return false;
		}
		//return true;
		return false;
	}

	public static File downloadFile(String path, String dest) throws IOException
	{
		URL url = new URL(path);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(new File(dest));
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		return new File(dest);
	}

	@Deprecated
	public static File downloadFile(URL url, String dest) throws IOException
	{
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(new File(dest));
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		return new File(dest);
	}

}
