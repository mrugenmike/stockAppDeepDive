package stockapp

import java.io.File
import java.util.Random

import akka.actor._
import com.typesafe.config.ConfigFactory


@SerialVersionUID(13l) case class StockPrice(symbol:String,price:Double)
@SerialVersionUID(14l) case class StockRequest(symbol:String)
@SerialVersionUID(15l) case class Message(message:String)

class StockServer extends Actor{
  val random:Random = new Random(2000)
  override def receive: Receive = {
    case msg: String => {
      println("remote received " + msg + " from " + sender)
      sender ! "hi Harshad got it dude!"
    }
    case StockRequest(symbol) => {
      println("Sending Price for %s".format(symbol) )
      sender ! StockPrice(symbol, random.nextDouble())
    }
    case Message(echo) => sender ! Message(echo+"from server")
  }
}

class StockClient extends Actor{
  override def receive: Actor.Receive = {
    case StockPrice(symbol,price)=> {
      println("Received the stock price for %s at %s".format(symbol,price))
    }

    case Message(msg) => print("found the message $msg")
  }
}

object StockServerApp {
def main(args:Array[String]): Unit ={
print("Server here!")
  //get the configuration file from classpath
  val configFile = getClass.getClassLoader.getResource("application.conf").getFile
  //parse the config
  val config = ConfigFactory.parseFile(new File(configFile))
  //create an actor system with that config
  val system = ActorSystem("RemoteSystem" , config)
  //create a remote actor from actorSystem
  val remote = system.actorOf(Props[StockServer], name="stockServer")
  println("remote is ready"+remote)
}
}




