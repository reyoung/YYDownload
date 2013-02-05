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

  @Parameter(names=Array("-d","--definition"),description = "video definition")
  var Definition="normal"
}

object Main {
  def main(args:Array[String]){
    new JCommander(Args, args.toArray: _*)

    if (Args.Verbose){
      println("Download Video Count : "+Args.URL.size())
    }
    for (url <- Args.URL.toArray()){
      if (Args.Verbose){
        println("Downloading "+url)
      }
      val parser = new YoukuParser()
      val definition = Args.Definition.toLowerCase() match {
        case "normal" => VideoDefinition.NORMAL
        case "high" => VideoDefinition.HIGH
        case "super" => VideoDefinition.SUPER
        case _ => null
      }
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
}
