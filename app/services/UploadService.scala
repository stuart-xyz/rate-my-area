package services

import java.io.File

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.PutObjectRequest
import play.api.Configuration

class UploadService(appConfig: Configuration) {

  private val regionName = appConfig.getOptional[String]("s3-region").getOrElse(throw new RuntimeException("s3-region configuration value is required"))
  private val bucketName = appConfig.getOptional[String]("s3-bucket-name").getOrElse(throw new RuntimeException("s3-bucket-name configuration value is required"))
  private val accessKey = appConfig.getOptional[String]("s3-access-key").getOrElse(throw new RuntimeException("s3-access-key configuration value is required"))
  private val secretKey = appConfig.getOptional[String]("s3-secret-key").getOrElse(throw new RuntimeException("s3-secret-key configuration value is required"))
  private val credentials = new BasicAWSCredentials(accessKey, secretKey)
  private val client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(regionName).build()

  def upload(file: File, filename: String, userId: Int): String = {
    val key = s"$userId/$filename"
    client.putObject(new PutObjectRequest(bucketName, key, file))
    client.getUrl(bucketName, key).toString
  }

}
