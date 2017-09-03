package modules

import java.net.URL

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.s3.model.{PutObjectRequest, PutObjectResult}
import play.api.Configuration

class S3ClientWrapper(appConfig: Configuration, mode: play.api.Mode) {

  private val client: Option[AmazonS3] =
    if (mode == play.api.Mode.Test) None
    else {
      val regionName = appConfig.getOptional[String]("s3-region").getOrElse(throw new RuntimeException("s3-region configuration value is required"))
      val accessKey = appConfig.getOptional[String]("s3-access-key").getOrElse(throw new RuntimeException("s3-access-key configuration value is required"))
      val secretKey = appConfig.getOptional[String]("s3-secret-key").getOrElse(throw new RuntimeException("s3-secret-key configuration value is required"))
      val credentials = new BasicAWSCredentials(accessKey, secretKey)
      Some(AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(regionName).build())
    }

  val configuredBucketName: String =
    if (mode == play.api.Mode.Test) ""
    else appConfig.getOptional[String]("s3-bucket-name").getOrElse(throw new RuntimeException("s3-bucket-name configuration value is required"))

  def putObject(putObjectRequest: PutObjectRequest): PutObjectResult = {
    if (client.nonEmpty) client.get.putObject(putObjectRequest)
    else new PutObjectResult()
  }

  def getUrl(bucketName: String, key: String): URL = {
    if (client.nonEmpty) client.get.getUrl(bucketName, key)
    else new URL("")
  }

}
