package awstools.check;

import awstools.AWSClients;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;

import java.util.HashMap;
import java.util.List;

public class CheckExists
{
  public static HashMap<String, Boolean> whichRegionServiceExist(AWSClients clients, String serviceName)
    throws AmazonServiceException
  {
    Regions[] regions = Regions.values();
    
    HashMap<String, Boolean> exists = new HashMap();
    
    System.out.println("\nWhere is " + serviceName + " used in the region:");
    for (int regionNo = 0; regionNo < regions.length; regionNo++) {
      if (clients.isUse(serviceName, regions[regionNo]))
      {
        System.out.println("RegionName:" + regions[regionNo].getName() + ":use");
        exists.put(regions[regionNo].getName(), Boolean.TRUE);
      }
      else
      {
        System.out.println("RegionName:" + regions[regionNo].getName() + ":-");
        exists.put(regions[regionNo].getName(), Boolean.FALSE);
      }
    }
    return exists;
  }
  
  public static void whichRegionsS3Exist()
    throws AmazonServiceException
  {
    System.out.println("\nWhere is S3 used in the region:");
    
    AmazonS3Client s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
    List<Bucket> buckets = s3.listBuckets();
    for (Bucket bucket : buckets) {
      System.out.println("RegionName:" + s3.getBucketLocation(bucket.getName()) + " bucketName:" + bucket.getName());
    }
  }
}
