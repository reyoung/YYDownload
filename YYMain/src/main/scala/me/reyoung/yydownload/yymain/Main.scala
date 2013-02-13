package me.reyoung.yydownload.yymain

import com.beust.jcommander.{ParameterException, IParameterValidator, JCommander, Parameter}
import me.reyoung.yydownload.yyparser._
import java.net.URL
import java.io.File
import com.github.axet.wget.WGet
import java.util.concurrent.atomic.AtomicBoolean
import com.github.axet.wget.info.DownloadInfo
import com.github.axet.wget.info.URLInfo.States
import com.github.axet.wget.info.DownloadInfo.Part
import collection.mutable.ListBuffer
import me.reyoung.yydownload.yyvideo.{OpenOutputFileStatus, FlvVideoMerger}

//import me.reyoung.yydownload.yyparser.YoukuParser

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/5/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
object Args{
  class DefinitionValidator extends IParameterValidator{
    def validate(name: String, value: String) {
      value.toLowerCase match {
        case "normal" | "high" | "super" => {}
        case _ => {throw new ParameterException("value must in NORMAL|HIGH|SUPER")}
      }
    }
  }

//  @Parameter(names = Array("-m"),description = "enable multithread download")
//  var Multithread = false

  @Parameter(description = "video url",required = true)
  var URL:java.util.List[String] = null

  @Parameter(names = Array("-o","--outpath"),description = "download output path")
  var Outpath = "./"

  @Parameter(names=Array("-v"),description = "verbose mode")
  var Verbose = false

  @Parameter(names=Array("-d","--definition"),description = "video definition <NORMAL|HIGH|SUPER>")
  var Definition="normal"


  @Parameter(names = Array("-w","--wget"),description =
    "Using shell wget command for download")
  var UsingWget = false

  @Parameter(names = Array("-r","--result"),description = "print parsed result, not download")
  var PrintRawResult = false

  @Parameter(names = Array("-h","--help"),description = "show this message",help=true)
  var Help = false

  @Parameter(names = Array("-l","--list"),description = "explicit download video list")
  var List = false

  @Parameter(names = Array("-V","--video"),description = "explicit download video")
  var Video = false

  @Parameter(names = Array("-m","--merge"),description = "merge the download video")
  var Merge = false

  def getDefinition = Args.Definition.toLowerCase match {
    case "normal" => VideoDefinition.NORMAL
    case "high" => VideoDefinition.HIGH
    case "super" => VideoDefinition.SUPER
    case _ => null
  }
}

object Main {

  def downloadProcess() {
    for (url <- Args.URL.toArray){
      if (Args.Verbose){
        println("Downloading "+url)
      }
      val result = parse(url)
      def downloadPR(pr:IParseResult){
        var totalSz = 0
        pr.DownloadUrls().foreach[Unit]((v:(URL,Int))=>{totalSz += v._2})
        println("Download "+pr.getTitle + " total size "+totalSz+" bytes")
        var count = 0
        for(v<-pr.DownloadUrls()){
          count+=1
          println("Download Part "+count)
          val targetFN=Args.Outpath+"%s%d.%s".format(pr.getTitle,count,pr.FileExtName)
          val targetFile = new File(targetFN)
          val d_info = new DownloadInfo(v._1)
          var d_count_1 = 0
          var d_count_2 = 0
          val notify = new Runnable {
            def run() {
              d_info.getState match{
                case States.DOWNLOADING =>{
                  d_count_1 += 1
                  if (d_count_1%32==0){
                    printf(".")
                    d_count_2+=1
                    if (d_count_2%72==0){
                      printf("%d%%\n",(100*d_info.getCount/d_info.getLength.toFloat).asInstanceOf[Int])
                      d_count_2=0
                    }
                    d_count_1=0
                  }
                }
                case States.DONE =>{
                  println()
                  println("Finish")
                }
                case _ =>{
                  // Not Interested
                }
              }
            }
          }

          val nostop =new AtomicBoolean(false)
          d_info.extract(nostop,notify)
          val wget = new WGet(d_info,targetFile)
          //        dinfo.enableMultipart()
          println("Saving to "+targetFN)
          wget.download(nostop,notify)
        }
        if(Args.Merge){
          pr.FileExtName() match {
            case "flv"=>{
              val outputs = new ListBuffer[File]
              for (count <- 1 to pr.DownloadUrls().length) {
                val ofn =Args.Outpath+"%s%d.%s".format(pr.getTitle,count,pr.FileExtName)
                outputs.+=(new File(ofn))
              }
              val ofn = Args.Outpath+"%s.%s".format(pr.getTitle,pr.FileExtName)
              val flvMerger = new FlvVideoMerger
              var d_count=0
              flvMerger.merge(new File(ofn),status =>{
                status match {
                  case _:OpenOutputFileStatus=>{
                    println("Start merging...\n")
                  }
                  case _=>{
                    if(status.isOk()){
                      d_count+=1
                      print(".")
                      if(d_count%75==0){
                        d_count = 0
                        println()
                      }
                    } else {
                      println("Fatal Error! %s \n",status.getStatusStr())
                      System.exit(1)
                    }

                  }
                }
              },outputs.toSeq:_*)
              println()
              outputs.foreach(f=>f.delete())
            }
            case _ => {
              println("Not Support Merge This Video")
            }
          }
        }
      }

      if (result.isDefined){
        result.get match {
          case pr:IParseResult=>downloadPR(pr)
          case ipr:IListParseResult=>{
            printf("Download Video List %s\n",ipr.Title())
            for (v<-ipr.Videos()){
              downloadPR(v)
            }
          }
          case _ =>{
            printf("parse error\n")
          }
        }
      } else {
        printf("parse error\n")
      }

    }
  }

  def parse(url:Any) =
    if (Args.List)
      ParserFactory.parseList(url.asInstanceOf[String], Args.getDefinition)
    else if (Args.Video)
      ParserFactory.parseVideo(url.asInstanceOf[String], Args.getDefinition)
    else
      ParserFactory.parse(url.asInstanceOf[String],Args.getDefinition)

  def printWgetCommand(){
    for (url <- Args.URL.toArray ){
      val result:Option[Any] = parse(url)
      if (result.isDefined){
        def printWgetParams(pr:IParseResult){
          var count = 0
          for (v <- pr.DownloadUrls ){
            count +=1
            val targetFN = Args.Outpath+"%s%d.%s".format(pr.getTitle,count,pr.FileExtName)
            printf("wget '%s' -U 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)' -O '%s'\n",v._1,targetFN)
          }
        }
        result.get match {
          case pr:IParseResult=>printWgetParams(pr)
          case lpr:IListParseResult=>
            for (pr<-lpr.Videos())
              printWgetParams(pr)
        }
      }
    }
  }

  def printRawResult(){
    var url_count = 0
    for (url <- Args.URL.toArray){
      url_count+=1
      val result = parse(url)
      def printParseResult(pr:IParseResult){
        printf("================================\n")
        printf("Video Title %s, Type %s, download url infos:\n",pr.getTitle,pr.FileExtName)
        for (u <- pr.DownloadUrls()){
          println(u)
        }
        printf("================================\n")
      }
      printf("=======================================\n")
      printf("Parsed URL %d (%s) with %s definition\n",url_count,url,Args.Definition)
      if (result.isDefined)
        result.get match {
          case pr:IParseResult=>{
            printf("The url parsed as Single Video \n")
            printParseResult(pr)
          }
          case lpr:IListParseResult =>{
            printf("The url parsed as List \n")
            printf("List Name is %s, list video info: \n",lpr.Title())
            for (pr <- lpr.Videos())
              printParseResult(pr)
          }
          case _ => {
            printf("parse error\n")
          }
        }
      else
        println("Cannot Parse This url")
      printf("=======================================\n")
    }
  }

  def main(args:Array[String]){
    val jc = new JCommander(Args, args.toArray: _*)
    if (Args.Help){
      jc.usage()
    } else if (Args.UsingWget){
      printWgetCommand()
    } else if (Args.PrintRawResult){
      printRawResult()
    } else {
      downloadProcess()
    }

  }
}
