import controllers.HomeController
import play.api.mvc.ControllerComponents

trait AppModule {

  import com.softwaremill.macwire._

  lazy val homeController: HomeController = wire[HomeController]

  def controllerComponents: ControllerComponents

}
