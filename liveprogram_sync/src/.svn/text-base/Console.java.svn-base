import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public class Console
{
	private static Kernel32 INSTANCE = null;

	public interface Kernel32 extends StdCallLibrary {
		public Pointer GetStdHandle(int nStdHandle);

		public boolean WriteConsoleW(Pointer hConsoleOutput, char[] lpBuffer,
				int nNumberOfCharsToWrite,
				IntByReference lpNumberOfCharsWritten, Pointer lpReserved);
	}

	static {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("win")) {
			INSTANCE = (Kernel32) Native
					.loadLibrary("kernel32", Kernel32.class);
		}
	}

	public static void print(String message) {
		if (!prePrint(message))
			System.out.print(message);

	}

	protected static boolean prePrint(String message) {
		boolean successful = false;
		if (INSTANCE != null) {
			Pointer handle = INSTANCE.GetStdHandle(-11);
			char[] buffer = message.toCharArray();
			IntByReference lpNumberOfCharsWritten = new IntByReference();
			successful = INSTANCE.WriteConsoleW(handle, buffer, buffer.length,
					lpNumberOfCharsWritten, null);
		}
		return successful;
	}

	public static void println(String message) {
		// from
		// http://stackoverflow.com/questions/54952/java-utf-8-and-windows-console
		if (prePrint(message)) {
			System.out.println();
		} else {
			System.out.println(message);
		}
	}
}
