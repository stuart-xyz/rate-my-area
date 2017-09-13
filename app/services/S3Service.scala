package services

import java.io.File

import com.amazonaws.services.s3.model.PutObjectRequest
import modules.S3ClientWrapper
import play.api.Configuration

class S3Service(appConfig: Configuration, s3Client: S3ClientWrapper) {

  def upload(file: File, filename: String, userId: Int): String = {
    val key = s"$userId/$filename"
    s3Client.putObject(new PutObjectRequest(s3Client.configuredBucketName, key, file))
    s3Client.getUrl(s3Client.configuredBucketName, key).toString
  }

  def delete(url: String): Unit = {
    s3Client.deleteObject(s3Client.configuredBucketName, url.split("/").last)
  }

}
