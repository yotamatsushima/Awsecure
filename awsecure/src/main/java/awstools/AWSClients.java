package awstools;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearchClient;

import com.amazonaws.services.datapipeline.DataPipelineClient;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import com.amazonaws.services.ec2.AmazonEC2Client;

import com.amazonaws.services.elasticache.AmazonElastiCacheClient;

import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;

import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.rds.AmazonRDSClient;

import com.amazonaws.services.redshift.AmazonRedshiftClient;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNSClient;

import com.amazonaws.services.sqs.AmazonSQSClient;

import java.util.HashMap;


public class AWSClients
{
  private HashMap<String, AmazonWebServiceClient> clients = new HashMap();
  
  public AWSClients()
  { 
    this.clients.put("ec2", new AmazonEC2Client(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("s3", new AmazonS3Client(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("dynamodb", new AmazonDynamoDBClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("rds", new AmazonRDSClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("cloudsearch", new AmazonCloudSearchClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("sqs", new AmazonSQSClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("sns", new AmazonSNSClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("elasticache", new AmazonElastiCacheClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("elasticmapreduce", new AmazonElasticMapReduceClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("elasticloadbalancing", new AmazonElasticLoadBalancingClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("redshift", new AmazonRedshiftClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("datapipeline", new DataPipelineClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("cloudfront", new AmazonCloudFrontClient(new DefaultAWSCredentialsProviderChain()));
    this.clients.put("iam", new AmazonIdentityManagementClient(new DefaultAWSCredentialsProviderChain()));
  }
  
  public AmazonWebServiceClient getClient(String serviceName)
  {
    return (AmazonWebServiceClient)this.clients.get(serviceName);
  }
  
  public boolean isUse(String serviceName, Regions regions)
  {
    AmazonWebServiceClient client = getClient(serviceName);
    if (client == null) {
      return false;
    }
    if ((!Region.getRegion(regions).isServiceSupported(serviceName)) || 
      (regions.equals(Regions.GovCloud)) || 
      (regions.equals(Regions.CN_NORTH_1))) {
      return false;
    }
    client.setRegion(regions);
    if (serviceName.equals("ec2")) {
      return !((AmazonEC2Client)client).describeInstances().getReservations().isEmpty();
    }
    if (!serviceName.equals("s3"))
    {
      if (serviceName.equals("dynamodb")) {
        return !((AmazonDynamoDBClient)client).listTables().getTableNames().isEmpty();
      }
      if (serviceName.equals("rds")) {
        return !((AmazonRDSClient)client).describeDBInstances().getDBInstances().isEmpty();
      }
      if (serviceName.equals("cloudsearch")) {
        return !((AmazonCloudSearchClient)client).describeDomains().getDomainStatusList().isEmpty();
      }
      if (serviceName.equals("sqs")) {
        return !((AmazonSQSClient)client).listQueues().getQueueUrls().isEmpty();
      }
      if (serviceName.equals("sns")) {
        return !((AmazonSNSClient)client).listTopics().getTopics().isEmpty();
      }
      if (serviceName.equals("elasticache")) {
        return !((AmazonElastiCacheClient)client).describeCacheClusters().getCacheClusters().isEmpty();
      }
      if (serviceName.equals("elasticmapreduce")) {
        return !((AmazonElasticMapReduceClient)client).listClusters().getClusters().isEmpty();
      }
      if (serviceName.equals("elasticloadbalancing")) {
        return !((AmazonElasticLoadBalancingClient)client).describeLoadBalancers().getLoadBalancerDescriptions().isEmpty();
      }
      if (serviceName.equals("redshift")) {
        return !((AmazonRedshiftClient)client).describeClusters().getClusters().isEmpty();
      }
      if (serviceName.equals("datapipeline")) {
        return !((DataPipelineClient)client).listPipelines().getPipelineIdList().isEmpty();
      }
      if (!serviceName.equals("cloudfront")) {}
    }
    return false;
  }
}
