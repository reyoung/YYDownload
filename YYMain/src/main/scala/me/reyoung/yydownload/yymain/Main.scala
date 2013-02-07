package me.reyoung.yydownload.yymain

import com.beust.jcommander.{ParameterException, IParameterValidator, JCommander, Parameter}
import me.reyoung.yydownload.yyparser.{VideoDefinition, YoukuParser}
import java.net.URL
import java.io.File
import com.github.axet.wget.WGet
import java.util.concurrent.atomic.AtomicBoolean
import com.github.axet.wget.info.DownloadInfo
import com.github.axet.wget.info.URLInfo.States
import com.github.axet.wget.info.DownloadInfo.Part

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

  @Parameter(names = Array("-m"),description = "enable multithread download")
  var Multithread = false

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
      val parser = YoukuParser
      val definition = Args.getDefinition
      val result = parser.parse(url.asInstanceOf[String],definition)
      var totalSz = 0
      result.DownloadUrls().foreach[Unit]((v:(URL,Int))=>{totalSz += v._2})
      println("Download "+result.getTitle + " total size "+totalSz+" bytes")
      var count = 0
      for(v<-result.DownloadUrls()){
        count+=1
        println("Download Part "+count)
        val targetFN=Args.Outpath+"%s%d.flv".format(result.getTitle,count)
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
    }
  }

  def printWgetCommand(){
    for (url <- Args.URL.toArray ){
      val parser = YoukuParser
      val defi = Args.getDefinition
      val result = parser.parse(url.asInstanceOf[String],defi)
      var count = 0
      for (v <- result.DownloadUrls ){
        count +=1
        val targetFN = Args.Outpath+"%s%d.flv".format(result.getTitle,count)
        printf("wget '%s' -U 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)' -O '%s'\n",v._1,targetFN)
      }
    }
  }

  def printRawResult(){
    var url_count = 0
    for (url <- Args.URL.toArray){
      url_count+=1
      val parser = YoukuParser
      val defi = Args.getDefinition
      val result = parser.parse(url.asInstanceOf[String],defi)
      printf("=======================================\n")
      printf("Parsed URL %d ( %s ) with %s definition\n",url_count,url.asInstanceOf[String],
        defi.toString)
      printf("Video Title: %s , download url info:\n",result.getTitle)
      for (v <- result.DownloadUrls){
        printf("%s , size %d bytes\n",v._1,v._2)
      }
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
