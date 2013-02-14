package me.reyoung.yydownload.yyvideomerger

import com.beust.jcommander.{JCommander, Parameter}

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/14/13
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
object Main  {
  object Args{
    @Parameter(description = "input video files",required = true)
    var URL:java.util.List[String] = null

    @Parameter(names=Array("-h","--help"),description = "show usage",help = true)
    var Help:Boolean=false
  }
  def main(args:Array[String]) {
    val jc = new JCommander(Args,args:_*)
    if (Args.Help){
      jc.usage()
    } else {
      /**
       * Main Handler
       */
    }
  }
}
