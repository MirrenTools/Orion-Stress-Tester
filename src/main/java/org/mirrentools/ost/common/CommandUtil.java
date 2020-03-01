package org.mirrentools.ost.common;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 常用指令工具类
 * 
 * @author 许畅
 * @since JDK1.7
 * @version 2018年9月14日 许畅 新建
 */
public class CommandUtil {

	/**
	 * 打开浏览器
	 * 
	 * @param uri
	 *          uri地址
	 * @return 是否成功
	 */
	public static boolean browse(URI uri) {
		if (openSystemSpecific(uri.toString())) {
			return true;
		}

		if (browseDESKTOP(uri)) {
			return true;
		}

		return false;
	}

	/**
	 * 打开文件
	 * 
	 * @param file
	 *          文件
	 * @return 是否成功
	 */
	public static boolean open(File file) {
		if (openSystemSpecific(file.getPath())) {
			return true;
		}

		if (openDESKTOP(file)) {
			return true;
		}

		return false;
	}

	/**
	 * 编辑指令
	 * 
	 * @param file
	 *          文件
	 * @return 是否成功
	 */
	public static boolean edit(File file) {
		// you can try something like
		// runCommand("gimp", "%s", file.getPath())
		// based on user preferences.

		if (openSystemSpecific(file.getPath())) {
			return true;
		}

		if (editDESKTOP(file)) {
			return true;
		}

		return false;
	}

	/**
	 * 打开系统特殊的指令
	 * 
	 * @param what
	 *          指令
	 * @return 是否成功
	 */
	private static boolean openSystemSpecific(String what) {
		EnumOS os = getOs();

		if (os.isLinux()) {
			if (runCommand("kde-open", "%s", what))
				return true;
			if (runCommand("gnome-open", "%s", what))
				return true;
			if (runCommand("xdg-open", "%s", what))
				return true;
		}

		if (os.isMac()) {
			if (runCommand("open", "%s", what))
				return true;
		}

		if (os.isWindows()) {
			if (runCommand("explorer", "%s", what))
				return true;
		}

		return false;
	}

	/**
	 * @param uri
	 *          URI
	 * @return boolean
	 */
	private static boolean browseDESKTOP(URI uri) {
		try {
			if (!Desktop.isDesktopSupported()) {
				return false;
			}

			if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				return false;
			}

			Desktop.getDesktop().browse(uri);

			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	/**
	 * @param file
	 *          File
	 * @return boolean
	 */
	private static boolean openDESKTOP(File file) {

		try {
			if (!Desktop.isDesktopSupported()) {
				return false;
			}

			if (!Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
				return false;
			}

			Desktop.getDesktop().open(file);

			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	/**
	 * 调用jdk工具类
	 * 
	 * @param file
	 *          File
	 * @return boolean
	 */
	private static boolean editDESKTOP(File file) {
		try {
			if (!Desktop.isDesktopSupported()) {
				return false;
			}

			if (!Desktop.getDesktop().isSupported(Desktop.Action.EDIT)) {
				return false;
			}
			Desktop.getDesktop().edit(file);

			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	/**
	 * 运行命令
	 * 
	 * @param command
	 *          指令
	 * @param args
	 *          参数
	 * @param file
	 *          文件
	 * @return 是否
	 */
	private static boolean runCommand(String command, String args, String file) {
		String[] parts = prepareCommand(command, args, file);

		try {
			Process p = Runtime.getRuntime().exec(parts);
			if (p == null)
				return false;

			try {
				int retval = p.exitValue();
				if (retval == 0) {
					return false;
				}
				return false;
			} catch (IllegalThreadStateException itse) {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 准备命令
	 * 
	 * @param command
	 *          指令
	 * @param args
	 *          参数
	 * @param file
	 *          文件
	 * @return string array
	 */
	private static String[] prepareCommand(String command, String args, String file) {
		List<String> parts = new ArrayList<>();
		parts.add(command);

		if (args != null) {
			for (String s : args.split(" ")) {
				s = String.format(s, file); // put in the filename thing

				parts.add(s.trim());
			}
		}

		return parts.toArray(new String[parts.size()]);
	}

	/**
	 * 操作系统
	 * 
	 * @author 许畅
	 * @since JDK1.7
	 * @version 2018年9月14日 许畅 新建
	 */
	public static enum EnumOS {
		/** linux系统 */
		linux,

		/** macos系统 */
		macos,

		/** solaris系统 */
		solaris,

		/** unknown系统 */
		unknown,

		/** windows系统 */
		windows;

		/**
		 * @return 是否linux
		 */
		public boolean isLinux() {
			return this == linux || this == solaris;
		}

		/**
		 * @return 是否mac
		 */
		public boolean isMac() {
			return this == macos;
		}

		/**
		 * @return 是否windows
		 */
		public boolean isWindows() {
			return this == windows;
		}
	}

	/**
	 * @return EnumOS
	 */
	public static EnumOS getOs() {
		String s = System.getProperty("os.name").toLowerCase();

		if (s.contains("win")) {
			return EnumOS.windows;
		}

		if (s.contains("mac")) {
			return EnumOS.macos;
		}

		if (s.contains("solaris")) {
			return EnumOS.solaris;
		}

		if (s.contains("sunos")) {
			return EnumOS.solaris;
		}

		if (s.contains("linux")) {
			return EnumOS.linux;
		}

		if (s.contains("unix")) {
			return EnumOS.linux;
		}
		return EnumOS.unknown;
	}
}
