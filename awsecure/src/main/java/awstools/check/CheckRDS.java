package awstools.check;

import awstools.AWSClients;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;

import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;

import com.amazonaws.services.rds.model.VpcSecurityGroupMembership;

public class CheckRDS
  extends CheckService
{
  private AmazonRDSClient rdsclient;
  private AmazonEC2Client ec2client;
  
  public void load(AWSClients clients, Regions regions)
  {
    this.rdsclient = ((AmazonRDSClient)clients.getClient("rds"));
    this.ec2client = ((AmazonEC2Client)clients.getClient("ec2"));
  }
  
  public String display()
  {
    writeln("RDS Security Group Check", true, 1);
    for (DBInstance dbi : this.rdsclient.describeDBInstances().getDBInstances()) {
      for (VpcSecurityGroupMembership sgid : dbi.getVpcSecurityGroups())
      {
        DescribeSecurityGroupsRequest sgrequest = new DescribeSecurityGroupsRequest();
        sgrequest.withGroupIds(new String[] { sgid.getVpcSecurityGroupId() });
        displaySecurityGroup(this.ec2client.describeSecurityGroups(sgrequest).getSecurityGroups(), 1);
      }
    }
    closeFolder(1);
    return getHtmlString();
  }
}
