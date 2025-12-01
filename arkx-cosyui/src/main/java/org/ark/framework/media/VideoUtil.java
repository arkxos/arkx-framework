package org.ark.framework.media;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.LogUtil;
import org.ark.framework.jaf.controls.UploadUI;

import java.io.*;

public class VideoUtil {
	public static final String _ConvertAvi2Flv = " -of lavf -oac mp3lame -lameopts abr:br=56 -ovc lavc -lavcopts vcodec=flv:vbitrate=200:mbd=2:mv0:trell:v4mv:cbp:last_pred=3:dia=4:cmp=6:vb_strategy=1 -vf scale=512:-3 -ofps 12\t -srate 22050";
	public static final String _ConvertRm2Flv = " -of lavf -oac mp3lame -lameopts abr:br=56 -ovc lavc -lavcopts vcodec=flv:vbitrate=200:mbd=2:mv0:trell:v4mv:cbp:last_pred=3 -srate 22050";
	public static final String _Identify = " -nosound -vc dummy -vo null";

	public static boolean captureDefaultImage(String src, String destImage, int duration) {
		if (duration < 30) {
			return captureImage(src, destImage, duration / 3);
		}
		return captureImage(src, destImage, 15);
	}

	public static boolean captureImage(String src, String destImage, int startSecond) {
		return captureImage(src, destImage, startSecond, 240, 180);
	}

	public static boolean captureImage(String src, String destImage, int ss, int width, int height) {
		String fileName = "ffmpeg";
		if (Config.getOSName().toLowerCase().indexOf("windows") >= 0) {
			fileName = "\"" + Config.getContextRealPath() + "Tools/" + "ffmpeg.exe\" ";
			src = "\"" + src + "\"";
			destImage = "\"" + destImage + "\"";
		}
		src = FileUtil.normalizePath(src);
		String command = fileName + " -i " + src + " -y -f image2 -ss " + ss + " -t 0.001 -s " + width + "*" + height + " " + destImage;
		return exec(command, src, null);
	}

	public static boolean convert2Flv(String src, String dest, AfterVideoConvertEvent event) {
		String fileName = "mencoder ";
		if (Config.getOSName().toLowerCase().indexOf("windows") >= 0) {
			fileName = "\"" + Config.getContextRealPath() + "Tools/" + "mencoder.exe\" ";
			src = "\"" + src + "\"";
			dest = "\"" + dest + "\"";
		}
		src = FileUtil.normalizePath(src);
		if (src.toLowerCase().lastIndexOf(".flv") != -1)
			return true;
		if ((src.toLowerCase().lastIndexOf(".rm") != -1) || (src.toLowerCase().lastIndexOf(".rmvb") != -1)) {
			return exec(fileName + src + " -o " + dest + " "
					+ " -of lavf -oac mp3lame -lameopts abr:br=56 -ovc lavc -lavcopts vcodec=flv:vbitrate=200:mbd=2:mv0:trell:v4mv:cbp:last_pred=3 -srate 22050", src, event);
		}
		return exec(
				fileName
						+ src
						+ " -o "
						+ dest
						+ " "
						+ " -of lavf -oac mp3lame -lameopts abr:br=56 -ovc lavc -lavcopts vcodec=flv:vbitrate=200:mbd=2:mv0:trell:v4mv:cbp:last_pred=3:dia=4:cmp=6:vb_strategy=1 -vf scale=512:-3 -ofps 12\t -srate 22050",
				src, event);
	}

	public static boolean convert2FlvSlit(String src, String dest, int ss, int endpos, AfterVideoConvertEvent event) {
		String fileName = "mencoder ";
		if (Config.getOSName().toLowerCase().indexOf("windows") >= 0) {
			fileName = "\"" + Config.getContextRealPath() + "Tools/" + "mencoder.exe\" ";
			src = "\"" + src + "\"";
			dest = "\"" + dest + "\"";
		}
		src = FileUtil.normalizePath(src);
		if ((src.toLowerCase().lastIndexOf(".rm") != -1) || (src.toLowerCase().lastIndexOf(".rmvb") != -1)) {
			return exec(fileName + src + " -o " + dest + " "
					+ " -of lavf -oac mp3lame -lameopts abr:br=56 -ovc lavc -lavcopts vcodec=flv:vbitrate=200:mbd=2:mv0:trell:v4mv:cbp:last_pred=3 -srate 22050", src, event);
		}
		return exec(
				fileName
						+ src
						+ " -o "
						+ dest
						+ " -ss "
						+ ss
						+ " -endpos "
						+ endpos
						+ " "
						+ " -of lavf -oac mp3lame -lameopts abr:br=56 -ovc lavc -lavcopts vcodec=flv:vbitrate=200:mbd=2:mv0:trell:v4mv:cbp:last_pred=3:dia=4:cmp=6:vb_strategy=1 -vf scale=512:-3 -ofps 12\t -srate 22050",
				src, event);
	}

	public static int getDuration(String src) {
		String fileName = "mplayer ";
		if (Config.getOSName().toLowerCase().indexOf("windows") >= 0) {
			fileName = "\"" + Config.getContextRealPath() + "Tools/" + "mplayer.exe\" ";
			src = "\"" + src + "\"";
		}
		String command = fileName + " -identify " + src + " " + " -nosound -vc dummy -vo null";
		int duration = 0;
		try {
			LogUtil.info("Video.getDuration:" + command);
			Process process = Runtime.getRuntime().exec(command, null, new File(Config.getContextRealPath() + "Tools/"));

			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					System.out.println(line);
					if ("libavformat file format detected".equals(line)) {
						break;
					}
					if (line.indexOf("ID_LENGTH=") > -1)
						duration = (int) Math.ceil(Double.parseDouble(line.substring(10)));
				}
			} catch (IOException e) {
				e.printStackTrace();
				return duration;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return duration;
		}
		LogUtil.info("VodeoUtil duration:" + duration);
		return duration;
	}

	public static int[] getWidthHeight(String src) {
		String fileName = "mplayer ";
		if (Config.getOSName().toLowerCase().toLowerCase().indexOf("windows") >= 0) {
			fileName = "\"" + Config.getContextRealPath() + "Tools/" + "mplayer.exe\" ";
			src = "\"" + src + "\"";
		}
		String command = fileName + " -identify " + src + " " + " -nosound -vc dummy -vo null";
		int[] WidthHeight = new int[2];
		try {
			LogUtil.info("VideoUtil.getWidthHeight:" + command);
			Process process = Runtime.getRuntime().exec(command, null, new File(Config.getContextRealPath() + "Tools/"));

			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					System.out.println(line);
					if (line.indexOf("ID_VIDEO_WIDTH=") > -1) {
						WidthHeight[0] = (int) Math.ceil(Double.parseDouble(line.substring(15)));
					}
					if (line.indexOf("ID_VIDEO_HEIGHT=") > -1)
						WidthHeight[1] = (int) Math.ceil(Double.parseDouble(line.substring(16)));
				}
			} catch (IOException e) {
				e.printStackTrace();
				return WidthHeight;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return WidthHeight;
		}
		LogUtil.info("VideoUtil WidthHeight:" + WidthHeight[0] + "x" + WidthHeight[1]);
		return WidthHeight;
	}

	public static boolean exec(String command, final String src,final AfterVideoConvertEvent event) {
		try {
			LogUtil.info("VideoUtil.exec:" + command);
			Process process = Runtime.getRuntime().exec(command, null, new File(Config.getContextRealPath() + "Tools/"));

			final InputStream is1 = process.getInputStream();
			new Thread(new Runnable() {
				public void run() {
					BufferedReader br = new BufferedReader(new InputStreamReader(is1));
					String line = null;
					try {
						while ((line = br.readLine()) != null) {
							UploadUI.setTask(src, line);
							System.out.println(line);
						}
						UploadUI.removeTask(src);
						if (event != null)
							event.afterConvert(src);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static abstract interface AfterVideoConvertEvent {
		public abstract void afterConvert(String paramString);
	}
}