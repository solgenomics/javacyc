// UnixDomainSocket.java
// J-BUDS version 1.0
// Copyright (c) 2001; Echogent Systems, Inc.
// See COPYRIGHT file for license details

// Modified on 05/29/2003 by Thomas Yan to replace deprecated thread code

import java.io.*;

/**
 * This class provides a means of connecting to a unix domain socket server.
 *
 * @author Robert Morgan
 */

public class UnixDomainSocket
{
	static
	{
	 	// Load the Unix Domain Socket C library
	    System.out.println("Reading library file: " + 
			       System.mapLibraryName("unixdomainsocket"));
		System.loadLibrary("unixdomainsocket");
	}
	
	// Input and output streams
	private UnixDomainSocketInputStream in;
	private UnixDomainSocketOutputStream out;

	// Socket read timeout
	private int timeout;
	
	// Native methods, implemented in the Unix Domain Socket C library
	private native static int nativeOpen(String socketFile);
	private native static int nativeRead(int nativeSocketFileHandle);
	private native static int nativeWrite(int nativeSocketFileHandle, int data);
	private native static void nativeClose(int nativeSocketFileHandle);
	private native static void nativeCloseInput(int nativeSocketFileHandle);
	private native static void nativeCloseOutput(int nativeSocketFileHandle);
	
	// Handle for the native Unix Domain Socket
	private int nativeSocketFileHandle;
	
	/**
	 * Creates a unix domain socket and connects it to the server specified by the socket file.
	 *
	 * @param socketFile Name of the socket file
	 * 
	 * @throws IOException If unable to construct the socket
 	 *
	 */
	public UnixDomainSocket(String socketFile)
	throws IOException
	{
	 	// Create the native socket, and connect using the specified socket file
		if( (nativeSocketFileHandle = nativeOpen(socketFile)) < 0)
	 	{
	 		throw new IOException("Unable to open Unix Domain Socket");
	 	}
	 	
	 	// Initialise the socket input and output streams
		in = new UnixDomainSocketInputStream();
	 	out = new UnixDomainSocketOutputStream();
	}
	
	/**
	 * Returns an input stream for this socket.
	 *
	 * @return An input stream for reading bytes from this socket
	 * 
	 */
	public InputStream getInputStream()
	{
	 	return (InputStream)in;
	}
	
	/**
	 * Returns an output stream for this socket.
	 *
	 * @return An output stream for writing bytes to this socket
	 * 
	 */
	public OutputStream getOutputStream()
	{
		return (OutputStream)out;
	}

	/**
	 * Sets the read timeout for the socket. If a read call blocks for the specified amount of time
	 * it will be cancelled, and a java.io.InterruptedIOException will be thrown. A timeout of zero
	 * is interpreted as an infinite timeout.
	 *
	 * @param timeout The specified timeout, in milliseconds.
	 */
	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}
	
	/**
	 * Closes the socket.
	 */
	public void close()
	{
	 	nativeClose(nativeSocketFileHandle);
	}
	
	private class UnixDomainSocketInputStream extends InputStream
	{
		// Reads a byte of data from the socket input stream
		public int read()
	 	throws IOException
	 	{
	 		int data;
			
			// If a timeout is set, then use a read thread
			if(timeout>0)
			{
				// Create a thread to read the byte
				UnixDomainSocketReadThread thread = new UnixDomainSocketReadThread();
				thread.setDaemon(true);
				thread.start();
				
				try
				{
					// Wait up until the specified timeout for the thread to complete
					thread.join(timeout);
				}
				catch(InterruptedException e)
				{}

				// If the thread is still alive, then the read() call has blocked longer than
				// the specified timeout
				if(thread.isAlive())
				{
				        //thread.stop();  This has been deprecated!
				        //Just leave thread alone until OS timeout occurs
					throw new InterruptedIOException("Unix Domain Socket read() call timed out");
				}
				else
				{
					data = thread.getData();
				}
				
			}
			else
			{
				data = nativeRead(nativeSocketFileHandle);
			}

			return data;
			
	 	}
		
		// Closes the socket input stream
		public void close()
		throws IOException
		{
			nativeCloseInput(nativeSocketFileHandle);
		}
	}
	
	private class UnixDomainSocketOutputStream extends OutputStream
	{
	 	// Write a byte of data to the socket output stream
		public void write(int data)
		throws IOException
		{		
			if((nativeWrite(nativeSocketFileHandle, data))<0)
			{
				throw new IOException("Unable to write to Unix Domain Socket");										
			}
		}
	 	 	
	 	// Closes the socket output stream
		public void close()
	 	throws IOException
	 	{
			nativeCloseOutput(nativeSocketFileHandle);
	 	}
	}

	// Thread class reads a byte of data from the socket. Used for enforcing timeouts.
	private class UnixDomainSocketReadThread extends Thread
	{
		private int data;

		public void run()
		{
			data = nativeRead(nativeSocketFileHandle);
		}	

		public int getData()
		{
			return data;
		}
	}

}


