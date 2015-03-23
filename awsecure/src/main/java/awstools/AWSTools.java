package awstools;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearchClient;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.redshift.AmazonRedshiftClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;

public class AWSTools
{
  public static void checkSeverityLevels()
    throws AmazonServiceException
  {}
  
  public static void whichRegionEc2Exist()
    throws AmazonServiceException
  {
    Regions[] regions = Regions.values();
    
    System.out.println("\nwhich part of Regions ec2 exist are  ");
    AmazonEC2Client ec2 = new AmazonEC2Client(new DefaultAWSCredentialsProviderChain());
    for (int regionNo = 0; regionNo < regions.length; regionNo++) {
      if ((Region.getRegion(regions[regionNo]).isServiceSupported("ec2")) && 
        (!regions[regionNo].equals(Regions.GovCloud)) && 
        (!regions[regionNo].equals(Regions.CN_NORTH_1)))
      {
        ec2.setRegion(regions[regionNo]);
        DescribeInstancesResult descInstResult = ec2.describeInstances();
        if (!descInstResult.getReservations().isEmpty()) {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":use");
        } else {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":-");
        }
      }
    }
  }
  
  public static void whichRegionsEBSExists()
    throws AmazonServiceException
  {
    Regions[] regions = Regions.values();
    System.out.println("\nwhich part of Regions ebs exist are  ");
    AmazonEC2Client ec2 = new AmazonEC2Client(new DefaultAWSCredentialsProviderChain());
    for (int regionNo = 0; regionNo < regions.length; regionNo++) {
      if ((Region.getRegion(regions[regionNo]).isServiceSupported("ec2")) && 
        (!regions[regionNo].equals(Regions.GovCloud)) && 
        (!regions[regionNo].equals(Regions.CN_NORTH_1)))
      {
        ec2.setRegion(regions[regionNo]);
        DescribeVolumesResult descEbsResult = ec2.describeVolumes();
        if (!descEbsResult.getVolumes().isEmpty()) {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":use");
        } else {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":-");
        }
      }
    }
  }
  
  public static void whichRegionsS3Exist()
    throws AmazonServiceException
  {
    System.out.println("\nwhich part of Regions s3 exist are  ");
    
    AmazonS3Client s3 = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
    List<Bucket> buckets = s3.listBuckets();
    for (Bucket bucket : buckets) {
      System.out.println("RegionName:" + s3.getBucketLocation(bucket.getName()) + " bucketName:" + bucket.getName());
    }
  }
  
  public static void whichRegionsDynamoExist()
    throws AmazonServiceException
  {
    Regions[] regions = Regions.values();
    
    System.out.println("\nwhich part of Regions dynamo exist are  ");
    AmazonDynamoDBClient dynamo = new AmazonDynamoDBClient(new DefaultAWSCredentialsProviderChain());
    for (int regionNo = 0; regionNo < regions.length; regionNo++) {
      if ((Region.getRegion(regions[regionNo]).isServiceSupported("dynamodb")) && 
        (!regions[regionNo].equals(Regions.GovCloud)) && 
        (!regions[regionNo].equals(Regions.CN_NORTH_1)))
      {
        dynamo.setRegion(regions[regionNo]);
        ListTablesResult tables = dynamo.listTables();
        if (tables.getTableNames().isEmpty()) {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":-");
        } else {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":use");
        }
      }
    }
  }
  
  public static void whichRegionsRdsExist()
    throws AmazonServiceException
  {
    Regions[] regions = Regions.values();
    
    System.out.println("\nwhich part of Regions RDS exist are  ");
    AmazonRDSClient rds = new AmazonRDSClient(new DefaultAWSCredentialsProviderChain());
    for (int regionNo = 0; regionNo < regions.length; regionNo++) {
      if ((Region.getRegion(regions[regionNo]).isServiceSupported("rds")) && 
        (!regions[regionNo].equals(Regions.GovCloud)) && 
        (!regions[regionNo].equals(Regions.CN_NORTH_1)))
      {
        rds.setRegion(regions[regionNo]);
        if (rds.describeDBInstances().getDBInstances().isEmpty()) {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":-");
        } else {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":use");
        }
      }
    }
  }
  
  public static void whichRegionsCloudSearchExist()
    throws AmazonServiceException
  {
    Regions[] regions = Regions.values();
    
    System.out.println("\nwhich part of Regions cloudsearch exist are  ");
    AmazonCloudSearchClient cloudsearch = new AmazonCloudSearchClient(new DefaultAWSCredentialsProviderChain());
    for (int regionNo = 0; regionNo < regions.length; regionNo++) {
      if ((Region.getRegion(regions[regionNo]).isServiceSupported("cloudsearch")) && 
        (!regions[regionNo].equals(Regions.GovCloud)) && 
        (!regions[regionNo].equals(Regions.CN_NORTH_1)))
      {
        cloudsearch.setRegion(regions[regionNo]);
        if (cloudsearch.describeDomains().getDomainStatusList().isEmpty()) {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":-");
        } else {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":use");
        }
      }
    }
  }
  
  public static void whichRegionsELBExist()
    throws AmazonServiceException
  {
    Regions[] regions = Regions.values();
    
    System.out.println("\nwhich part of Regions elb exist are  ");
    AmazonElasticLoadBalancingClient elb = new AmazonElasticLoadBalancingClient(new DefaultAWSCredentialsProviderChain());
    for (int regionNo = 0; regionNo < regions.length; regionNo++) {
      if ((Region.getRegion(regions[regionNo]).isServiceSupported("elasticloadbalancing")) && 
        (!regions[regionNo].equals(Regions.GovCloud)) && 
        (!regions[regionNo].equals(Regions.CN_NORTH_1)))
      {
        elb.setRegion(regions[regionNo]);
        if (elb.describeLoadBalancers().getLoadBalancerDescriptions().isEmpty()) {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":-");
        } else {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":use");
        }
      }
    }
  }
  
  public static void whichRegionsRedshiftExist()
    throws AmazonServiceException
  {
    Regions[] regions = Regions.values();
    
    System.out.println("\nwhich part of Regions redshift exist are  ");
    AmazonRedshiftClient redshift = new AmazonRedshiftClient(new DefaultAWSCredentialsProviderChain());
    for (int regionNo = 0; regionNo < regions.length; regionNo++) {
      if ((Region.getRegion(regions[regionNo]).isServiceSupported("redshift")) && 
        (!regions[regionNo].equals(Regions.GovCloud)) && 
        (!regions[regionNo].equals(Regions.CN_NORTH_1)))
      {
        redshift.setRegion(regions[regionNo]);
        if (redshift.describeClusters().getClusters().isEmpty()) {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":-");
        } else {
          System.out.println("RegionName:" + regions[regionNo].getName() + ":use");
        }
      }
    }
  }
  
  public static String getEc2TagValue(List<Tag> tags, String key)
  {
    String str = "";
    for (Tag tag : tags) {
      if (tag.getKey().toUpperCase().equals(key.toUpperCase()))
      {
        str = tag.getValue();
        break;
      }
    }
    return str;
  }
}
