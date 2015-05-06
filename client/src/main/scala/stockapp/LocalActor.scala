package stockapp

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor._
import com.typesafe.config.ConfigFactory

/**
 * Local actor which listens on any free port
 */

@SerialVersionUID(13l)
case class StockPrice(symbol:String,price:Double)

@SerialVersionUID(14l)
case class StockRequest(symbol:String)

@SerialVersionUID(15l)
case  class Message(message:String)
 class LocalActor extends Actor {


  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {


  }
  override def receive: Receive = {


    case  StockPrice(symbol, price) =>{

      println("stock price received is:" + price)
    }
    case  message:String=>{

    println("Message price received is:" + message)
  }
    case  Message(message) =>{

      println("stock price received is:" + message)
    }

  }
}



object ClientApp {

  def main(args: Array[String]) {






    import scala.concurrent.duration._
    val clientConfigFile = getClass.getClassLoader.getResource("application.conf").getFile
    val clientConfig = ConfigFactory.parseFile(new File(clientConfigFile))
    val clientsystem = ActorSystem("ClientSystem",clientConfig)
    val stockClient = clientsystem.actorOf(Props[LocalActor],name="stockClient")
    val remoteActor = clientsystem.actorFor("akka.tcp://RemoteSystem@10.189.172.199:5150/user/stockServer")

    clientsystem.scheduler.schedule(0.seconds, 3.second, remoteActor,StockRequest("Infy") )(clientsystem.dispatcher, stockClient)
  }


}
