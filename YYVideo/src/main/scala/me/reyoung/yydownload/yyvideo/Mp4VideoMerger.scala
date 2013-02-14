package me.reyoung.yydownload.yyvideo

import java.io.{RandomAccessFile, FileInputStream, File}
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.builder.{FragmentedMp4Builder, DefaultMp4Builder}
import com.googlecode.mp4parser.authoring.{Movie, Track}
import java.util
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/14/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
class Mp4VideoMerger extends IVideoMerger{

//  val mc = new MovieCreator
  object MP4InputVideoStatus{
    val PARSING_VIDEO_FRAME=1
    val PARSING_AUDIO_FRAME=2
    val parsing_video_frame = new MP4InputVideoStatus(PARSING_VIDEO_FRAME)
    val parsing_audio_frame = new MP4InputVideoStatus(PARSING_AUDIO_FRAME)
  }
  case class MP4InputVideoStatus(override val Status:Int)extends InputVideoStatus(Status){
    override def getStatusStr()
    = Status match {
      case MP4InputVideoStatus.PARSING_VIDEO_FRAME => "Parsing Video Frame"
      case MP4InputVideoStatus.PARSING_AUDIO_FRAME => "Parsing Audio Frame"
      case _ => super.getStatusStr()
    }
  }
  def merge(output: File, callback: (MergeStatus) => Unit, videoFiles: File*): Boolean = {
    val mv_out = new Movie
    val videoTracks = new ListBuffer[Track]
    val audioTracks = new ListBuffer[Track]
    for (input <- videoFiles){
      callback(new OpenInputFileStatus(input,if (!input.exists()){-1} else if (input.isDirectory){-2} else {0}))
      val mv = MovieCreator.build(new FileInputStream(input).getChannel)
      for (track <- mv.getTracks.toArray if track.isInstanceOf[Track]){
        val tk = track.asInstanceOf[Track]
        if (tk.getHandler == "soun"){
          callback(MP4InputVideoStatus.parsing_audio_frame)
          audioTracks += tk
        } else if (tk.getHandler == "vide"){
          callback(MP4InputVideoStatus.parsing_video_frame)
          videoTracks += tk
        }
      }
    }
    mv_out.addTrack(new AppendTrack(videoTracks:_*))
    mv_out.addTrack(new AppendTrack(audioTracks:_*))
    val out = new DefaultMp4Builder().build(mv_out)
    val outfile = new RandomAccessFile(output,"rw").getChannel
    outfile.position(0)
    out.getBox(outfile)
    outfile.close()
    true
  }
}
