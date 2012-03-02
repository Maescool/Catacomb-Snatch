package com.mojang.mojam;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class Console implements Runnable
{
	JTextArea displayPane;
	BufferedReader reader;

	private Console(JTextArea displayPane, PipedOutputStream pos)
	{
		this.displayPane = displayPane;

		try
		{
			PipedInputStream pis = new PipedInputStream(pos);
			reader = new BufferedReader(new InputStreamReader(pis));
		}
		catch (IOException e)
		{
		}
	}

	public void run()
	{
		String line = null;

		try
		{
			while((line = reader.readLine()) != null)
			{
				//              displayPane.replaceSelection( line + "\n" );
				displayPane.append(line + "\n");
				displayPane.setCaretPosition(displayPane.getDocument().getLength());
			}

			System.err.println("im here");
		}
		catch (IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Error redirecting output : " + ioe.getMessage());
		}
	}

	public static void redirectOutput(JTextArea displayPane)
	{
		Console.redirectOut(displayPane);
		Console.redirectErr(displayPane);
	}

	public static void redirectOut(JTextArea displayPane)
	{
		PipedOutputStream pos = new PipedOutputStream();
		System.setOut(new PrintStream(pos, true));

		Console console = new Console(displayPane, pos);
		new Thread(console).start();
	}

	public static void redirectErr(JTextArea displayPane)
	{
		PipedOutputStream pos = new PipedOutputStream();
		System.setErr(new PrintStream(pos, true));

		Console console = new Console(displayPane, pos);
		new Thread(console).start();
	}

	public static void main(String[] args)
	{
		JTextArea textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);

		JFrame frame = new JFrame("Redirect Output");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(scrollPane);
		frame.setSize(300, 600);
		frame.setVisible(true);

		Console.redirectOutput(textArea);
		final int i = 0;

		Timer timer = new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//System.out.println( new java.util.Date().toString() );
				//System.err.println( System.currentTimeMillis() );
			}
		});
		timer.start();
	}
}