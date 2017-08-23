import _root_.controllers.AssetsComponents
import com.softwaremill.macwire._
import modules.{ControllerModule, DatabaseModule}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.slick.evolutions.SlickEvolutionsComponents
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes

import scala.concurrent.ExecutionContext

class AppApplicationLoader extends ApplicationLoader {

  override def load(context: Context): Application = {
    new AppComponents(context).application
  }

}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with HttpFiltersComponents
  with AssetsComponents
  with DatabaseModule
  with ControllerModule {

  LoggerConfigurator(context.environment.classLoader).foreach { configurator =>
    configurator.configure(context.environment)
  }

  // this will actually run the database migrations on startup
  applicationEvolutions

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  lazy val router: Router = {
    implicit val prefix: String = "/"
    wire[Routes]
  }

}
