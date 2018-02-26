package services

import java.io.File
import java.net.URL

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.s3.model.PutObjectRequest
import play.api.Configuration

class S3Service(appConfig: Configuration) {

  lazy val configuredBucketName: String = appConfig.get[String]("s3-bucket-name")
  lazy val cloudFrontEnabled: Boolean = appConfig.get[Boolean]("cloudfront-enabled")
  lazy val regionName: String = appConfig.get[String]("s3-region")
  lazy val accessKey: String = appConfig.get[String]("s3-access-key")
  lazy val secretKey: String = appConfig.get[String]("s3-secret-key")
  lazy val credentials = new BasicAWSCredentials(accessKey, secretKey)

  lazy val s3Client: AmazonS3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(regionName).build()

  def upload(file: File, filename: String, userId: Int): String = {
    val key = s"$userId/$filename"
    s3Client.putObject(new PutObjectRequest(configuredBucketName, key, file))
    if (cloudFrontEnabled) s"https://$configuredBucketName/$key"
    else s"https://$configuredBucketName/$key"
  }

  def delete(url: String): Unit = {
    s3Client.deleteObject(configuredBucketName, new URL(url).getFile.stripPrefix("/"))
  }

}
