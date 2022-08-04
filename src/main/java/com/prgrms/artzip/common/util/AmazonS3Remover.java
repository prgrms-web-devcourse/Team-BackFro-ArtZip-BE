package com.prgrms.artzip.common.util;

import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmazonS3Remover {

  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public AmazonS3Remover(AmazonS3Client amazonS3Client) {
    this.amazonS3Client = amazonS3Client;
  }

  public void removeFile(String path, String dirName) {
    amazonS3Client.deleteObject(bucket, getKey(path, dirName));
  }

  private String getKey(String path, String dirName) {
    return path.substring(path.indexOf(dirName));
  }
}
