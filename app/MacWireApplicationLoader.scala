import _root_.controllers.AssetsComponents
import com.softwaremill.macwire._
import play.api.ApplicationLoader.Context
import play.api._
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes

class MacWireApplicationLoader extends ApplicationLoader {

  override def load(context: Context): Application = {
    new AppComponents(context).application
  }

}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with HttpFiltersComponents
  with AssetsComponents
  with AppModule {

  LoggerConfigurator(context.environment.classLoader).foreach { configurator =>
    configurator.configure(context.environment)
  }

  lazy val router: Router = {
    implicit val prefix: String = "/"
    wire[Routes]
  }

}
