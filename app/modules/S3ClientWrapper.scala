package modules

import java.net.URL

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.s3.model.{PutObjectRequest, PutObjectResult}
import play.api.Configuration

class S3ClientWrapper(appConfig: Configuration, mode: play.api.Mode) {

  private val cloudfrontUrl =
    if (mode == play.api.Mode.Test) ""
    else appConfig.get[String]("cloudfront-url")

  private val clientOption: Option[AmazonS3] =
    if (mode == play.api.Mode.Test) None
    else {
      val regionName = appConfig.get[String]("s3-region")
      val accessKey = appConfig.get[String]("s3-access-key")
      val secretKey = appConfig.get[String]("s3-secret-key")
      val credentials = new BasicAWSCredentials(accessKey, secretKey)
      Some(AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(regionName).build())
    }

  val configuredBucketName: String =
    if (mode == play.api.Mode.Test) ""
    else appConfig.getOptional[String]("s3-bucket-name").getOrElse(throw new RuntimeException("s3-bucket-name configuration value is required"))

  def putObject(putObjectRequest: PutObjectRequest): PutObjectResult = {
    if (clientOption.isDefined) clientOption.get.putObject(putObjectRequest)
    else new PutObjectResult()
  }

  def deleteObject(bucketName: String, key: String): Unit = clientOption.foreach(_.deleteObject(bucketName, key))

  def getUrl(bucketName: String, key: String): URL = new URL(s"$cloudfrontUrl/$key")

}
