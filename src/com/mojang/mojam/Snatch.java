package com.mojang.mojam;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JTextArea;

import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.network.NetworkCommand;
import com.mojang.mojam.network.Packet;
import com.mojang.mojam.network.packet.ChangeKeyCommand;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public final class Snatch
{
	private static boolean init = false;
	public static File modDir;
	public static File modsFolder;
	public static List<IMod> modList = new ArrayList<IMod>();
	public static List<ScriptEngine> scriptList = new ArrayList<ScriptEngine>();
	private static MojamComponent mojam;
	private static InputHandler inputHandler;
	private static Keys keys = new Keys();
	public static Map<Integer, Entity> spawnList = new HashMap<Integer, Entity>();
	private static ScriptEngineManager lang = new ScriptEngineManager();
	public static PipedOutputStream sysOut = new PipedOutputStream();
	public static JTextArea textArea;
	public static boolean isJar;
	public static boolean isDebug;

	public static void init(MojamComponent m)
	{
		if(init) return;
		init = true;
		mojam = m;
		keys.getAll().removeAll(keys.getAll());
		try
		{
			modDir = new File(Snatch.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			modsFolder = new File(mojam.getMojamDir(), "/mods/");
			isJar = modDir.getAbsolutePath().endsWith(".jar");
			isDebug = modDir.getAbsolutePath().contains("/bin") && !isJar;
		}
		catch (URISyntaxException e1)
		{
			e1.printStackTrace();
		}
		if(isJar||true)
		{
			displayConsoleWindow();
			addMod(Snatch.class.getClassLoader(),"Console.class");
		}
		//System.setOut(new PrintStream(new FileOutputStream(new File(m.getMojamDir(), "log.txt"))));

		System.out.println("Snatch starting up...");
		System.out.println(modDir.getAbsolutePath());
		addMod(Snatch.class.getClassLoader(), "SnatchContent.class");
		inputHandler = (InputHandler) reflectField(mojam, "inputHandler");
		try
		{
			readLinksFromFile(new File(mojam.getMojamDir(), "mods.txt"));
			//readFromModFolder(new File(mojam.getMojamDir(), "/mods/"));
			readFromModsFolder();
			readFromClassPath(modDir);
			//readLinksFromFile(new File(mojam.getMojamDir(),"mods.txt"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void readFromModsFolder()
	{
		File[] files = modsFolder.listFiles();
		boolean valid;
		for(File f : files)
		{
			valid = true;
			if(valid) addScript(f.getAbsolutePath());
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
						else if(!zipentry.isDirectory() && s1.startsWith("mod_") && !s1.contains(".MF"))
						{
							addScript(zipentry);
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
							System.out.println(afile1[k].getAbsolutePath());
							String s2 = afile1[k].getName();
							if(afile1[k].isFile() && s2.startsWith("mod_") && s2.endsWith(".class"))
							{
								addMod(classloader, s2);
							}
							else if(afile1[k].isFile() && s2.startsWith("mod_") && !s2.contains(".MF"))
							{
								addScript(s2);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return The MojamComponent instance, containing everything.
	 * @see MojamComponent
	 */
	public static MojamComponent getMojam()
	{
		return mojam;
	}

	private static IMod addMod(ClassLoader classloader, String s)
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
			if(!(IMod.class).isAssignableFrom(class1))
			{
				return null;
			}
			IMod mod = (IMod) class1.newInstance();
			if(mod != null)
			{
				modList.add(mod);
				System.out.println("Java Mod Initialized: " + mod.getClass().getSimpleName());
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
					addMod(classloader, s1);
				}
				else if(!zipentry.isDirectory() && s1.contains("mod_") && !s1.toLowerCase().endsWith(".mf"))
				{
					addScript(zipentry);
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
					else if(afile[i].isFile() && s2.contains("mod_") && !s2.toLowerCase().endsWith(".mf"))
					{
						addScript(afile[i].getAbsolutePath());
					}
				}
			}
		}
	}

	private static ScriptEngine addScript(String s)
	{
		ScriptEngine e = lang.getEngineByExtension(s.substring(s.lastIndexOf('.') + 1));
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

	private static ScriptEngine addScript(ZipEntry entry)
	{
		String s = entry.getName();
		ScriptEngine engine = lang.getEngineByExtension(s.substring(s.lastIndexOf('.') + 1));
		/*for(ScriptEngineFactory factory:lang.getEngineFactories())
		{
			System.out.println(factory.getLanguageName()+":"+factory.getEngineName());
			for(String s1:factory.getExtensions())
			{
				System.out.println("|__> "+ s1);
			}
		}*/
		if(engine == null || entry.getName().contains("MANIFEST")) return null;
		try
		{
			int BUFFER = 8192;
			BufferedOutputStream dest = null;
			BufferedInputStream is = null;
			ZipFile zipfile = new ZipFile(modDir.getAbsolutePath());
			Enumeration e = zipfile.entries();
			while(e.hasMoreElements())
			{
				entry = (ZipEntry) e.nextElement();
				System.out.println("Extracting: " + entry);
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				int count;
				byte data[] = new byte[BUFFER];
				FileOutputStream fos = new FileOutputStream(entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while((count = is.read(data, 0, BUFFER)) != -1)
				{
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
				FileReader fr = new FileReader(entry.getName());

				engine.eval(fr);
				engine.put("Snatch", new Snatch());
				scriptList.add(engine);
				System.out.println(engine.getFactory().getExtensions().get(0).toUpperCase() + " Script initialised: " + s);
			}
		}
		catch (FileNotFoundException e1)
		{
			if(!isJar) e1.printStackTrace();
		}
		catch (NullPointerException e1)
		{
			System.out.println("Could not initialise mod " + s);
		}
		catch (ScriptException e1)
		{
			System.out.println("Bad Script file: " + entry.getName());
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		return engine;
	}

	@Deprecated
	private static void addJSMod(ClassLoader c, String s)
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
		for(IMod m : modList)
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

	/**
	 * Registers an instance of the entity into
	 * a List and then returns its id, so that
	 * it can later be used for spawning custom
	 * entities.
	 * 
	 * @param entity
	 * @return The id of the registered entity
	 * @see Entity
	 */
	public static int addEntity(Entity entity)
	{
		spawnList.put(spawnList.size(), entity);
		int i = spawnList.size() - 1;
		System.out.println("Registered " + spawnList.get(i).getClass().getSimpleName() + " with id " + i);
		return i;
	}

	/**
	 * Registers a new key <br>
	 * {@code Key mykey = Snatch.addKey("aKey","a");}
	 * @param name The name of the key
	 * @param code The keyboard code of the key to be registered
	 * @return The registered key
	 */
	public static Key addkey(String name, String code)
	{
		return addKey(name, keycode(code));
	}
	
	/**
	 * Registers a new key <br>
	 * {@code Key mykey = Snatch.addKey("aKey",32);}
	 * @param name The name of the key
	 * @param code The keyboard code of the key to be registered
	 * @return The registered key
	 */
	public static Key addKey(String name, int code)
	{
		return addKey(keys.new Key(name),code);
	}
	
	/**
	 * Registers a given key
	 * @param key The key being registered
	 * @param code The keyboard code of the key being registered
	 * @return The key once registered
	 */
	public static Key addKey(Key key, int code)
	{
		inputHandler = (InputHandler) reflectField(mojam, "inputHandler");
		for(Keys k : mojam.synchedKeys)
		{
			k.getAll().add(key);
		}
		reflectMethod(InputHandler.class, inputHandler, "initKey", new Object[]
		{
			key,
			code
		});
		System.out.println("Added key: " + key.name + " with keycode: " + code);
		return key;
	}

	/**
	 * Gets the integer keycode for the name s
	 * @param s The name of the key
	 * @return The intcode of the key
	 */
	public static int keycode(String s)
	{
		Field f = (Field) reflectField(java.awt.event.KeyEvent.class, "VK_" + s.toUpperCase());
		try
		{
			return (Integer) f.get(null);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Used to add an animated bitmap
	 * 
	 * @param src
	 *            The location of the bitmap file
	 * @return A bitmap used for rendering. The Bitmap[] holds Bitmap[]s for
	 *         each animation, e.g. <br>
	 *         {@code
	 * Bitmap[][] myBitmap = addAnimation(srcString);
	 * return myBitmap[animationId][frameId];
	 * }
	 */
	public static Bitmap[][] addAnimation(String src)
	{
		return Art.cut(src, 32, 32);
	}

	/**
	 * Used to add a still-image bitmap
	 * 
	 * @param src
	 *            The location of the bitmap file
	 * @return A bitmap used for rendering
	 */
	public static Bitmap addArt(String src)
	{
		return load(src);
	}

	/**
	 * Invokes a reflected method
	 * 
	 * @param o
	 *            The instance that contains the method to be reflected
	 * @param s
	 *            The name of the method
	 * @param params
	 *            The parameters of the method
	 * @return The value that the method being reflected returns
	 */
	public static Object reflectMethod(Object o, String s, Object params[])
	{
		// Go and find the private method... 
		final Method methods[] = o.getClass().getDeclaredMethods();
		for(int i = 0; i < methods.length; ++i)
		{
			if(s.equals(methods[i].getName()))
			{
				try
				{
					methods[i].setAccessible(true);
					return methods[i].invoke(o, params);
				}
				catch (IllegalAccessException ex)
				{

				}
				catch (InvocationTargetException ite)
				{

				}
			}
		}
		return null;
	}

	/**
	 * Invokes a method in a declared class
	 * 
	 * @param c
	 *            The class the instance is of
	 * @param o
	 *            The instance of the class
	 * @param s
	 *            The name of the method
	 * @param params
	 *            The parameters of the method
	 * @return The returned value of the method
	 */
	public static Object reflectMethod(Class c, Object o, String s, Object params[])
	{
		// Go and find the private method... 
		final Method methods[] = c.getDeclaredMethods();
		for(int i = 0; i < methods.length; ++i)
		{
			if(s.equals(methods[i].getName()))
			{
				try
				{
					methods[i].setAccessible(true);
					return methods[i].invoke(o, params);
				}
				catch (IllegalAccessException ex)
				{

				}
				catch (InvocationTargetException ite)
				{

				}
			}
		}
		return null;
	}

	/**
	 * Sets a reflected field that would otherwise be hidden
	 * 
	 * @param o
	 *            The instance that holds the field to be reflected
	 * @param s
	 *            The name of the field to be set
	 * @param value
	 *            The new value for the field
	 * @return The field's new value. It is successful if it returns 'value'.
	 */
	public static Object reflectField(Object o, String s, Object value)
	{
		// Go and find the private field... 
		final Field fields[] = o.getClass().getDeclaredFields();
		for(int i = 0; i < fields.length; ++i)
		{
			if(s.equals(fields[i].getName()))
			{
				try
				{
					fields[i].setAccessible(true);
					fields[i].set(o, value);
					return fields[i].get(o);
				}
				catch (IllegalAccessException ex)
				{

				}
			}
		}
		return null;
	}

	/**
	 * Gets a reflected value that would otherwise be hidden
	 * 
	 * @param o
	 *            The instance that holds the field to be reflected
	 * @param s
	 *            The name of the field to be reflected
	 * @return the reflected value of the field
	 */
	public static Object reflectField(Object o, String s)
	{
		// Go and find the private fields... 
		final Field fields[] = o.getClass().getDeclaredFields();
		for(int i = 0; i < fields.length; ++i)
		{
			if(s.equals(fields[i].getName()))
			{
				try
				{
					fields[i].setAccessible(true);
					return fields[i].get(o);
				}
				catch (IllegalAccessException ex)
				{

				}
			}
		}
		return null;
	}

	/**
	 * Reflects a static field from a {@link Class}
	 * 
	 * @param c
	 *            The class to reflect the variable from
	 * @param s
	 *            The name of the variable
	 * @return The field reflected
	 */
	public static Object reflectField(Class c, String s)
	{
		// Go and find the private fields... 
		final Field fields[] = c.getDeclaredFields();
		for(int i = 0; i < fields.length; ++i)
		{
			if(s.equals(fields[i].getName()))
			{
				fields[i].setAccessible(true);
				return fields[i];
			}
		}
		return null;
	}

	private static Bitmap load(String string)
	{
		try
		{
			BufferedImage bi = ImageIO.read(MojamComponent.class.getResource(string));

			int w = bi.getWidth();
			int h = bi.getHeight();

			Bitmap result = new Bitmap(w, h);
			bi.getRGB(0, 0, w, h, result.pixels, 0, w);

			return result;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static void afterRender()
	{
		for(IMod m : modList)
		{
			m.OnRender();
		}
		invoke("OnRender");
	}

	public static void startRender()
	{
		for(IMod m : modList)
		{
			m.OnStartRender();
		}
		invoke("OnStartRender");
	}

	public static void afterTick()
	{
		for(IMod m : modList)
		{
			m.AfterTick();
		}
		invoke("AfterTick");
	}

	public static void runOnce()
	{
		for(IMod m : modList)
		{
			m.RunOnce();
		}
		invoke("RunOnce");
	}

	public static void createLevel(Level level)
	{
		for(IMod m : modList)
		{
			m.CreateLevel(level);
		}
		invoke("CreateLevel");
	}

	public static void onStop()
	{
		for(IMod m : modList)
		{
			m.OnClose();
		}
		invoke("OnStop");
	}

	public static void onWin(int i)
	{
		for(IMod m : modList)
		{
			m.OnVictory(i);
		}
		invoke("OnVictory", i);
	}

	public static void levelTick(Level level)
	{
		for(IMod m : modList)
		{
			m.OnLevelTick(level);
		}
		invoke("OnLevelTick", level);
	}

	public static void updateTick()
	{
		for(IMod m : modList)
		{
			m.OnTick();
		}
		invoke("OnTick");
	}

	public static void sendPacket(Packet packet)
	{
		for(IMod m : modList)
		{
			m.OnSendPacket(packet);
		}
		invoke("OnSendPacket", packet);
	}

	public static void receivePacket(Packet packet)
	{
		for(IMod m : modList)
		{
			m.OnReceivePacket(packet);
		}
		invoke("OnReceivePacket", packet);
	}

	public static void handlePacket(Packet packet)
	{
		for(IMod m : modList)
		{
			m.HandlePacket(packet);
		}
		invoke("HandlePacket", packet);
	}

	private static void invoke(String s, Object... args)
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
				//System.out.println("Bad method name: " + s);
			}
			catch (ScriptException e)
			{
				System.out.println("Bad method in file: " + e.getFileName() + " at method " + s);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the current system time
	 * 
	 * @return current system time in milliseconds
	 */
	public static long currentTimeMillis()
	{
		return System.currentTimeMillis();
	}

	/**
	 * Gets the current system time
	 * 
	 * @return current system time in nanoseconds
	 */
	public static long nanoTime()
	{
		return System.nanoTime();
	}

	/**
	 * Gets an instance of Font to avoid static method semantics in scripts
	 * 
	 * @return an instance of the class @see Font for non-statically calling
	 *         static code
	 */
	public static Font getFont()
	{
		return Font.defaultFont();
	}

	/**
	 * Returns a new instance of an Empty Entity for JS or others to manipulate;
	 * i.e: <br>
	 * {@code newEntity(x,y).getSpeed = new function return 1.5; }
	 * 
	 * @param x
	 *            x position to spawn
	 * @param y
	 *            y position to spawn
	 * @return new instance to modify
	 * @see Entity
	 * @see EmptyEntity
	 */
	public static Entity newEntity(double x, double y)
	{
		return new EmptyEntity(x, y);
	}

	public static void setGamemode(GameMode gamemode)
	{
		TitleMenu.defaultGameMode = gamemode;
	}

	private static void readLinksFromFile(File f) throws IOException
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
			if(!f1.exists()) f1.createNewFile();
			try
			{
				if(!upToDate(s))
				{
					File f2 = downloadFile(s, f1.getAbsolutePath());
					stringList.add(f2.getAbsolutePath());
				}
			}
			catch (Exception e)
			{

			}
		}
		for(String s : stringList)
		{
			//System.out.println(addScript(s));//TODO
			addScript(s);
		}
	}

	private static boolean upToDate(String s) throws IOException
	{
		File f = new File(s);
		File f1 = new File(mojam.getMojamDir(), "mods/" + s.substring(s.lastIndexOf('/') + 1));
		if(!f1.exists()) f1.mkdirs();
		f1.createNewFile();
		//System.out.println(f.hashCode() + ":" + f1.hashCode() + " - " + f.lastModified() + ":" + f1.lastModified());//TODO
		if(f.hashCode() == f1.hashCode() && f.lastModified() == f1.lastModified())
		{
			//System.out.println(f.hashCode() + ":" + f1.hashCode() + " - " + f.lastModified() + ":" + f1.lastModified());
			return true;
		}
		return false;
	}

	private static File downloadFile(String path, String dest) throws IOException
	{
		URL url = new URL(path);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(new File(dest));
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		return new File(dest);
	}

	private static void displayConsoleWindow()
	{
		Console.main(null);
	}

	/**
	 * Used for impromptu class casting in dynamic languages
	 * 
	 * @param d
	 *            var to pass
	 * @return d as long
	 */
	public static long asLong(double d)
	{
		return (long) d;
	}

	public static void handleNetworkCommand(int playerId, NetworkCommand packet)
	{
		if(packet instanceof ChangeKeyCommand)
		{
			ChangeKeyCommand ckc = (ChangeKeyCommand) packet;
			Key key = mojam.synchedKeys[playerId].getAll().get(ckc.getKey());
			if(key.isDown)
			{
				for(IMod m : modList)
				{
					m.IfKeyDown(key);
				}
			}
			else
			{
				for(IMod m : modList)
				{
					m.IfKeyUp(key);
				}
			}
		}
	}
	
	public static void console(String s) throws ScriptException
	{
		if(s.startsWith("echo "))
		{
			System.out.println(s.substring(5));
		}
		else if(s.startsWith("exit"))
		{
			try{
				System.exit(Integer.parseInt(s.substring(5)));
			} catch (NumberFormatException e)
			{
				System.exit(1);
			}
		}
		else if(s.startsWith("js ")||s.startsWith("py ")||s.startsWith("rb ")||s.startsWith("lua "))
		{
			lang.getEngineByExtension(s.substring(0, 3).trim()).eval(s.substring(s.indexOf(' '+1)));
		}
		else
		{
			String s1 = s.substring(0,s.indexOf(' ')+1);
			if(s1.length()==0)
			{
				System.out.println("Error: Unknown Command: "+s);
			} else {
				System.out.println("Error: Unknown Command: "+s1);
			}
		}
	}

}
