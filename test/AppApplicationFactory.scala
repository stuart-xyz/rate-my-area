import org.scalatestplus.play.FakeApplicationFactory
import play.api.inject.DefaultApplicationLifecycle
import play.api.{Application, ApplicationLoader, Configuration, Environment}
import play.core.DefaultWebCommands
import services.S3Service

trait AppApplicationFactory extends FakeApplicationFactory {

  class AppApplicationBuilder {
    def build(s3ServiceOverride: Option[S3Service] = None): Application = {
      val env = Environment.simple()
      val context = ApplicationLoader.Context(
        environment = env,
        sourceMapper = None,
        webCommands = new DefaultWebCommands(),
        initialConfiguration = Configuration.load(env),
        lifecycle = new DefaultApplicationLifecycle()
      )
      val loader = new AppApplicationLoader()
      loader.setS3ServiceOverride(s3ServiceOverride)
      loader.load(context)
    }
  }

  def fakeApplication(): Application = new AppApplicationBuilder().build()

}
