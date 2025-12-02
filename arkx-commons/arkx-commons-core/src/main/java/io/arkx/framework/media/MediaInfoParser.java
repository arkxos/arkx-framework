package io.arkx.framework.media;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.oro.text.regex.*;

import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.StringUtil;

public class MediaInfoParser {

	private int width = 0;

	private int height = 0;

	private String beginTime;

	private int vBitRate;

	private String vCodec;

	private String vFormat;

	private String resolution;

	private int duration;

	private String aCodec;

	private String aSampleRate;

	public void parse(String inputPath) {
		String result = processFile(inputPath);
		if (StringUtil.isEmpty(result)) {
			return;
		}
		PatternCompiler compiler = new Perl5Compiler();
		try {
			String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
			String regexVideo = "Video: (.*?), (.*?), (.*?)[,\\s]";
			String regexAudio = "Audio: (\\w*), (\\d*) Hz";

			Pattern patternDuration = compiler.compile(regexDuration, 1);
			PatternMatcher matcherDuration = new Perl5Matcher();
			if (matcherDuration.contains(result, patternDuration)) {
				MatchResult re = matcherDuration.getMatch();

				String durationStr = re.group(1);
				if (StringUtil.isEmpty(durationStr)) {
					durationStr = "00:00:00.0";
				}
				int h = Integer.parseInt(durationStr.substring(0, 2));
				int m = Integer.parseInt(durationStr.substring(3, 5));
				int s = Integer.parseInt(durationStr.substring(6, 8));
				this.duration = (h * 3600 + m * 60 + s);

				this.beginTime = re.group(2);
				this.vBitRate = Integer.valueOf(re.group(3)).intValue();
			}
			Pattern patternVideo = compiler.compile(regexVideo, 1);
			PatternMatcher matcherVideo = new Perl5Matcher();
			if (matcherVideo.contains(result, patternVideo)) {
				MatchResult re = matcherVideo.getMatch();

				this.vCodec = re.group(1);
				this.vFormat = re.group(2);
				this.resolution = re.group(3);
				if ((StringUtil.isNotEmpty(this.resolution)) && (this.resolution.indexOf("x") > 0)) {
					this.width = Integer.valueOf(this.resolution.split("x")[0]).intValue();
					this.height = Integer.valueOf(this.resolution.split("x")[1]).intValue();
				}
			}
			Pattern patternAudio = compiler.compile(regexAudio, 1);
			PatternMatcher matcherAudio = new Perl5Matcher();
			if (matcherAudio.contains(result, patternAudio)) {
				MatchResult re = matcherAudio.getMatch();

				this.aCodec = re.group(1);
				this.aSampleRate = re.group(2);
			}
		}
		catch (MalformedPatternException e) {
			e.printStackTrace();
		}
	}

	private String processFile(String inputPath) {
		FfmpegCommandBuilder fcb = new FfmpegCommandBuilder();
		fcb.addFfmpegCommand();
		fcb.addInputFileCommand(inputPath);
		try {
			ProcessBuilder builder = new ProcessBuilder(new String[0]);
			builder.command(fcb.getCommandList());
			builder.redirectErrorStream(true);
			Process p = builder.start();

			BufferedReader buf = null;
			String line = null;

			buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

			StringBuffer sb = new StringBuffer();
			while ((line = buf.readLine()) != null) {
				LogUtil.info(line);
				sb.append(line);
			}
			return sb.toString();
		}
		catch (Exception e) {
			LogUtil.info(e.getMessage());
		}
		return null;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getBeginTime() {
		return this.beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public int getvBitRate() {
		return this.vBitRate;
	}

	public void setvBitRate(int vBitRate) {
		this.vBitRate = vBitRate;
	}

	public String getvCodec() {
		return this.vCodec;
	}

	public void setvCodec(String vCodec) {
		this.vCodec = vCodec;
	}

	public String getvFormat() {
		return this.vFormat;
	}

	public void setvFormat(String vFormat) {
		this.vFormat = vFormat;
	}

	public String getResolution() {
		return this.resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public int getDuration() {
		return this.duration;
	}

	public String getDurationStr() {
		int hour = this.duration / 3600;
		int mins = (this.duration - hour * 3600) / 60;
		int seconds = this.duration - (hour * 3600 + mins * 60);
		return hour + ":" + mins + ":" + seconds;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getaCodec() {
		return this.aCodec;
	}

	public void setaCodec(String aCodec) {
		this.aCodec = aCodec;
	}

	public String getaSampleRate() {
		return this.aSampleRate;
	}

	public void setaSampleRate(String aSampleRate) {
		this.aSampleRate = aSampleRate;
	}

}
