package awstools;

import awstools.check.CheckEBS;
import awstools.check.CheckExists;
import awstools.check.CheckIAM;
import awstools.check.CheckRDS;
import awstools.check.CheckSupport;
import awstools.check.CheckVPCtoEc2;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import java.io.PrintStream;

public class awscure
{
  public static void main(String[] args)
  {
    try
    {
      StringBuffer htmlString = new StringBuffer();
      AWSClients clients = new AWSClients();
      
      CheckExists.whichRegionServiceExist(clients, "ec2");
      CheckExists.whichRegionServiceExist(clients, "dynamodb");
      
      CheckExists.whichRegionServiceExist(clients, "redshift");
      CheckExists.whichRegionServiceExist(clients, "cloudsearch");
      CheckExists.whichRegionServiceExist(clients, "elasticloadbalancing");
      CheckExists.whichRegionServiceExist(clients, "elasticache");
      CheckExists.whichRegionServiceExist(clients, "elasticmapreduce");
      CheckExists.whichRegionServiceExist(clients, "rds");
      CheckExists.whichRegionServiceExist(clients, "sns");
      CheckExists.whichRegionServiceExist(clients, "sqs");
      CheckExists.whichRegionServiceExist(clients, "datapipeline");
      

      CheckExists.whichRegionsS3Exist();
      
      int alert = 0;
      int ng = 0;
      CheckIAM iamcheck = new CheckIAM();
      iamcheck.load(clients, Regions.AP_NORTHEAST_1);
      htmlString.append(iamcheck.display());
      alert = iamcheck.getAlertcount();
      ng = iamcheck.getNGCount();
      

      CheckVPCtoEc2 ec2check = new CheckVPCtoEc2();
      
      ec2check.load(clients, Regions.AP_NORTHEAST_1);
      
      htmlString.append(ec2check.display());
      
      alert += ec2check.getAlertcount();
      ng += ec2check.getNGCount();
      

      CheckRDS rdscheck = new CheckRDS();
      rdscheck.load(clients, Regions.AP_NORTHEAST_1);
      htmlString.append(rdscheck.display());
      alert += rdscheck.getAlertcount();
      ng += rdscheck.getNGCount();
      
      CheckEBS ebscheck = new CheckEBS();
      ebscheck.load(clients, Regions.AP_NORTHEAST_1);
      htmlString.append(ebscheck.display());
      alert += ebscheck.getAlertcount();
      ng += ebscheck.getNGCount();
      
      CheckSupport supportcheck = new CheckSupport();
      supportcheck.load(clients, Regions.AP_NORTHEAST_1);
      htmlString.append(supportcheck.display());
      alert += supportcheck.getAlertcount();
      ng += supportcheck.getNGCount();
      

      System.out.println("Done.");
      System.out.println("NG:" + ng);
      System.out.println("Alert:" + alert);
    }
    catch (AmazonServiceException e)
    {
      e.printStackTrace();
      System.out.println("access deny!! Maybe.. You are wrong with  Environment Variables - check AWS_ACCESS_KEY_ID and AWS_SECRET_KEY!!");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
