package HelloServer

import java.io.File

import akka.actor._
import com.typesafe.config.ConfigFactory

class StockServer extends Actor{
  override def receive: Receive = {
    case msg: String => {
      println("remote received " + msg + " from " + sender)
      sender ! "hi"
    }
    case _ => println("Received unknown msg ")
  }
}

object StockServer {
def main(args:Array[String]): Unit ={
print("Server here!")
  //get the configuration file from classpath
  val configFile = getClass.getClassLoader.getResource("application.conf").getFile
  //parse the config
  val config = ConfigFactory.parseFile(new File(configFile))
  //create an actor system with that config
  val system = ActorSystem("RemoteSystem" , config)
  //create a remote actor from actorSystem
  val remote = system.actorOf(Props[StockServer], name="remote")
  println("remote is ready")

}
}




