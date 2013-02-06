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

You can download prebuilt version from [here](http://YYDownload.reyoung.me/YYCli-lastest.jar)

## How to use YYCli

YYCli contains three mode. There are simple download mode, wget script mode,raw result mode.

### Simple Download Mode
The sample command is `java -jar YYCli.jar "http://v.youku.com/v_show/id_XNTExNzI5MDI0.html" -d super -o /home/reyoung/`.

The option -d will define video definition, avaliable options are "normal", "high", "super".

The option -o will define the output path

The result of running these command like:
```
   Download 【第一次】魔兽争霸xiaoy解说骨灰杯玉米 vs th000 TM total size 162052111 bytes
   Download Part 1
   Saving to /home/reyoung/【第一次】魔兽争霸xiaoy解说骨灰杯玉米 vs th000 TM1.flv
   ........................................................................10%
   .........................................
```

### Wget Script Mode

Because of the jvm cost to much memory resource. The wget mode is recommanded for VPS user or user whose
computer don't have much memory. In these mode, YYCli will print some command to `wget`, and use `wget` to download

The wget mode just add an switch -w, the sample command and result shows below.

```
$ java -jar YYCli.jar "http://v.youku.com/v_show/id_XNTExNzI5MDI0.html" -d normal -o /home/reyoung/ -w | bash
--2013-02-06 21:06:39--  http://f.youku.com/player/getFlvPath/sid/00_00/st/flv/fileid/03000203005111F02AAC4D01B4746A1B35AE1F-16D0-3C55-AAC8-CFE2EC04B0C8?K=4e1ef6f5be4f9c2a261cd351
Resolving f.youku.com (f.youku.com)... 119.167.145.98, 119.167.145.106, 119.167.145.107, ...
Connecting to f.youku.com (f.youku.com)|119.167.145.98|:80... connected.
HTTP request sent, awaiting response... 302 Found
Location: http://119.188.0.135/youku/677400DAF1343831F4A1CF5D1F/03000203005111F02AAC4D01B4746A1B35AE1F-16D0-3C55-AAC8-CFE2EC04B0C8.flv [following]
--2013-02-06 21:06:40--  http://119.188.0.135/youku/677400DAF1343831F4A1CF5D1F/03000203005111F02AAC4D01B4746A1B35AE1F-16D0-3C55-AAC8-CFE2EC04B0C8.flv
Connecting to 119.188.0.135:80... connected.
HTTP request sent, awaiting response... 200 OK
Length: 13207340 (13M) [video/x-flv]
Saving to: `/home/reyoung/【第一次】魔兽争霸xiaoy解说骨灰杯玉米 vs th000 TM1.flv'

14% [===============>                                                                                                    ] 1,922,166    188K/s  eta 67s   
```

### Raw Result Mode

In these mode, it will not download anything, just parse the url and print the parsed result. It can used for other develop or debug just for me.
The switch is '-r', and sample command/result shows below.

```
$ java -jar YYCli.jar "http://v.youku.com/v_show/id_XNTExNzI5MDI0.html" -d normal -o /home/reyoung/ -r 
=======================================
Parsed URL 1 ( http://v.youku.com/v_show/id_XNTExNzI5MDI0.html ) with NORMAL definition
Video Title: 【第一次】魔兽争霸xiaoy解说骨灰杯玉米 vs th000 TM , download url info:
http://f.youku.com/player/getFlvPath/sid/00_00/st/flv/fileid/03000203005111F02AAC4D01B4746A1B35AE1F-16D0-3C55-AAC8-CFE2EC04B0C8?K=6f1c2571475d535e28285b0c , size 13207340 bytes
http://f.youku.com/player/getFlvPath/sid/00_01/st/flv/fileid/03000203015111F02AAC4D01B4746A1B35AE1F-16D0-3C55-AAC8-CFE2EC04B0C8?K=b9ab578f531cf19424114b97 , size 14102060 bytes
http://f.youku.com/player/getFlvPath/sid/00_02/st/flv/fileid/03000203025111F02AAC4D01B4746A1B35AE1F-16D0-3C55-AAC8-CFE2EC04B0C8?K=3e8b0ea5897b254e24114b97 , size 11398419 bytes
=======================================
```
