package stockapp

import java.io.File
import java.util.{Date, Random}

import akka.actor._
import akka.event.EventStream
import com.typesafe.config.ConfigFactory


@SerialVersionUID(13l) case class StockPrice(symbol:String,price:Double)
@SerialVersionUID(14l) case class StockRequest(symbol:String)
@SerialVersionUID(15l) case class Message(message:String)
@SerialVersionUID(16l) case class StockSubScribeRequest(symbol:String)
@SerialVersionUID(17l) case class SubscribeForSymbol(symbol:String)

class StockServer extends Actor{
  val random:Random = new Random(2000)

  val handlers = scala.collection.mutable.HashMap.empty[String,ActorRef]

  override def receive: Receive = {
    case msg: String => {
      println("remote received " + msg + " from " + sender)
      sender ! "hi Harshad got it dude!"
    }
    case StockRequest(symbol) => {
      println("Received StockRequest for %s".format(symbol))
      handlers(symbol) forward(StockRequest(symbol))
    }
    case StockSubScribeRequest(symbol) => {
      handlers contains(symbol) match{
        case true => {
          System.out.println("Received StockSubScribeRequest %s for pre-existing handler".format(symbol))
          handlers(symbol).forward(SubscribeForSymbol(symbol))
        }
        case false => {
          System.out.println("Received StockSubScribeRequest %s from Sender".format(symbol))
          val child: ActorRef = context.actorOf(Props[StockHandler], symbol)
          handlers += (symbol-> child)
          println("Delegating request for symbol %s to child handler ".format(symbol))
          child.tell(StockSubScribeRequest,sender)
          context.watch(child)
        }
      }
    }

    case Terminated(watched) => {
        handlers.remove(watched.path.name)
    }
    case Message(echo) => sender ! Message(echo+"from server")
  }
}

class StockHandler extends Actor{
  var stockSymbol:String = ""
  val clients  = scala.collection.mutable.ArrayBuffer.empty[ActorRef]
  var lastStockPrice = StockPrice("DUMMY",3000.0) // initial value

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
     stockSymbol = self.path.name
     context.system.eventStream.subscribe(self,classOf[StockPrice])
  }
  override def receive: Actor.Receive = {

    case SubscribeForSymbol(symbol) =>{
      clients += sender()
    }
    case StockPrice(symbol,price) => {
      symbol.equals(stockSymbol) match{
        case true=> {
          //update current stock price
          lastStockPrice = StockPrice(symbol,price)
          //intimate all subscribers
          println("received update for symbol %s - Broadcasting to all subscribers at time %s".format(symbol,System.currentTimeMillis()))
          clients.foreach(a=>a ! StockPrice(symbol,price))}
        case _ => //Not my symbol ignore the stock update
      }
    }
    case StockRequest(symbol)=>{
       println("Replying to StockRequest for symbol %s with latestStockPrice".format(symbol))
       sender! lastStockPrice
    }
  }
}

case class StockPricePublisher() extends Actor{
  val random:Random = new java.util.Random(2000);
  val symbols = List("INFY","TCS","AAPL","GOOG","MRF","TATA-STEEL","TATA-MOTORS","HINDALCO","WIPRO")

  def generateStockPrice()= {
     val symbol: String = util.Random.shuffle(symbols).head
     StockPrice(symbol,random.nextDouble()*1000)
  }

  override def receive: Actor.Receive = {
    case msg:String => {
      val generatedStockPrice: StockPrice = generateStockPrice()
      System.out.println("Publishing the generated stockprice %s at time %s ".format(generatedStockPrice,System.currentTimeMillis()))
      context.system.eventStream.publish(generatedStockPrice)
    }
  }
}

case class PublisherTrigger() extends Actor{
  override def receive: Actor.Receive = {
    case msg:String=> //do nothing
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
  val publisherTrigger = system.actorOf(Props[StockServer], name="publishTrigger")
  println("remote is ready"+remote)

  import concurrent.duration._
  val publisher: ActorRef = system.actorOf(Props[StockPricePublisher],"StockPricePublisher")
  system.scheduler.schedule(0.seconds,500.millisecond,publisher,"GenerateMessage")(system.dispatcher,publisherTrigger)
}



}




