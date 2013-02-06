# YYCli
YYCli is command line version of YYDownload, it can parse and download video from url of video site.
Currently, Youku is the only support site.

## How to get YYCli
You can build YYCli from source or get the lasted prebuilt version from web.

### Build from source
1. You should get the source from github, and put it into a directory, like "YYDownload".
2. You should install maven and have a bash environment(cygwin or msys for windows).
3. cd into the source directory, execute `build_jar.sh`, then in the source root there will be a file named "YYCli.jar"

### Get prebuilt from web

You can download prebuilt version from [here](http://www.baidu.com)

## How to use YYCli

YYCli contains three mode. There are simple download mode, wget script mode,raw result mode.

### Simple Download Mode
The sample command is `java -jar YYCli.jar "http://v.youku.com/v_show/id_XNTExNzI5MDI0.html" -d super -o /home/reyoung/`.

The option -d will define video definition, avaliable options are "normal", "high", "super".

The option -o will define the output path

The result of running these command like:
<pre><code>Download 【第一次】魔兽争霸xiaoy解说骨灰杯玉米 vs th000 TM total size 162052111 bytes
   Download Part 1
   Saving to /home/reyoung/【第一次】魔兽争霸xiaoy解说骨灰杯玉米 vs th000 TM1.flv
   ........................................................................10%
   .........................................
</code></pre>

### Wget Script Mode

Because of the jvm cost to much memory resource. The wget mode is recommanded for VPS user or user whose
computer don't have much memory. In these mode, YYCli will print some command to `wget`, and use `wget` to download