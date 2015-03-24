package awstools.check;

import awstools.AWSClients;
import awstools.Tools;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeNetworkAclsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.IamInstanceProfile;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.NetworkAclAssociation;
import com.amazonaws.services.ec2.model.NetworkAclEntry;
import com.amazonaws.services.ec2.model.PortRange;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import model.InstanceEx;
import model.SubnetEx;
import model.VpcEx;

public class CheckVPCtoEc2
  extends CheckService
{
  private AmazonEC2Client ec2client;
  private AmazonElasticLoadBalancingClient elbclient;
  private List<VpcEx> vpcexs = new ArrayList();
  
  public void load(AWSClients clients, Regions regions)
  {
    this.ec2client = ((AmazonEC2Client)clients.getClient("ec2"));
    this.ec2client.setRegion(Region.getRegion(regions));
    
    this.elbclient = ((AmazonElasticLoadBalancingClient)clients.getClient("elasticloadbalancing"));
    this.elbclient.setRegion(Region.getRegion(regions));
    load();
  }
  
  private void load()
  {
    loadVpc();
  }
  
  private void loadVpc()
  {
    DescribeVpcsResult vpcs = this.ec2client.describeVpcs();
    for (Vpc vpc : vpcs.getVpcs())
    {
      VpcEx vpcex = new VpcEx();
      vpcex.setVpc(vpc);
      loadSubnet(vpcex);
      loadELB(vpcex);
      this.vpcexs.add(vpcex);
    }
  }
  
  private void loadELB(VpcEx vpcex)
  {
    DescribeLoadBalancersResult elbs = this.elbclient.describeLoadBalancers();
    for (LoadBalancerDescription elb : elbs.getLoadBalancerDescriptions()) {
      if (elb.getVPCId().equals(vpcex.getVpc().getVpcId())) {
        vpcex.addELB(elb);
      }
    }
  }
  
  private void loadSubnet(VpcEx vpcex)
  {
    DescribeSubnetsResult subnets = this.ec2client.describeSubnets();
    for (Subnet subnet : subnets.getSubnets()) {
      if (subnet.getVpcId().equals(vpcex.getVpc().getVpcId()))
      {
        SubnetEx subnetex = new SubnetEx();
        subnetex.setSubnet(subnet);
        

        loadNacl(subnetex);
        
        loadInstance(subnetex);
        

        vpcex.addSubnet(subnetex);
      }
    }
  }
  
  private void loadNacl(SubnetEx subnetex)
  {
    DescribeNetworkAclsResult nacls = this.ec2client.describeNetworkAcls();
    for (NetworkAcl nacl: nacls.getNetworkAcls())
    {

      for (NetworkAclAssociation naclasso : nacl.getAssociations()) {
        if (subnetex.getSubnet().getSubnetId().equals(naclasso.getSubnetId())) {
          subnetex.addNacl(nacl);
        }
      }
    }
  }
  
  private void loadInstance(SubnetEx subnetex)
  {
    List<Reservation> reserves = this.ec2client.describeInstances().getReservations();
    for (Reservation reserve : reserves) {
      for (Instance instance : reserve.getInstances()) {
        if ((instance.getSubnetId() != null) && (instance.getSubnetId().equals(subnetex.getSubnet().getSubnetId())))
        {
          InstanceEx instanceex = new InstanceEx();
          
          instanceex.setInstance(instance);
          loadSecurityGroup(instanceex);
          subnetex.addInstance(instanceex);
        }
      }
    }
  }
  
  private void loadSecurityGroup(InstanceEx instanceex)
  {
    DescribeSecurityGroupsResult securityGroups = this.ec2client.describeSecurityGroups();
    for (SecurityGroup security : securityGroups.getSecurityGroups())
    {

      for (GroupIdentifier gident : instanceex.getInstance().getSecurityGroups()) {
        if (gident.getGroupId().equals(security.getGroupId())) {
          instanceex.addSG(security);
        }
      }
    }
    SecurityGroup security;
  }
  
  public String display()
  {
    writeln("\nsecurity information between VPC to EC2", true, 1);
    for (VpcEx vpcex : this.vpcexs)
    {
      writeln("VPC(" + vpcex.getVpc().getVpcId() + "):" + Tools.getTagValue(vpcex.getVpc().getTags(), "NAME"), true, 2);
      displayELB(vpcex);
      displaySubnet(vpcex);
      closeFolder(2);
    }
    closeFolder(1);
    return getHtmlString();
  }
  
  private void displayELB(VpcEx vpcex)
  {
    writeln("ELB Check", true, 3);
    for (LoadBalancerDescription elb : vpcex.getELBs()) {
      for (String sgid : elb.getSecurityGroups())
      {
        DescribeSecurityGroupsRequest sgrequest = new DescribeSecurityGroupsRequest();
        sgrequest.withGroupIds(new String[] { sgid });
        
        displaySecurityGroup(this.ec2client.describeSecurityGroups(sgrequest).getSecurityGroups(), 3);
      }
    }
    closeFolder(3);
  }
  
  private void displaySubnet(VpcEx vpcex)
  {
    writeln("Subnet Check", true, 3);
    for (SubnetEx subnetex : vpcex.getSubnets())
    {
      writeln("subnet(" + subnetex.getSubnet().getSubnetId() + "):" + Tools.getTagValue(subnetex.getSubnet().getTags(), "NAME"), true, 4);
      
      writeln("Acl Check", true, 5);
      for (NetworkAcl nacl : subnetex.getNacls()) {
        for (NetworkAclEntry aclentry : nacl.getEntries())
        {
          String output = "";
          output = output + "ruleNumber:" + aclentry.getRuleNumber();
          if (aclentry.getEgress().booleanValue()) {
            output = output + " outbound";
          } else {
            output = output + " inbound";
          }
          output = output + " cidr:" + aclentry.getCidrBlock();
          if (aclentry.getPortRange() != null) {
            output = output + " portRange:" + aclentry.getPortRange().toString();
          } else {
            output = output + " portRange:!!!ANY!!!!";
          }
          output = output + " ruleaction:" + aclentry.getRuleAction();
          if ((aclentry.getCidrBlock().equals("0.0.0.0/0")) && (aclentry.getPortRange() == null) && (aclentry.getRuleAction().toLowerCase().equals("allow"))) {
            output = output + setNG();
          }
          writeln(output, false, 6);
        }
      }
      closeFolder(5);
      for (InstanceEx instanceex : subnetex.getInstances())
      {
        writeln("instance(" + instanceex.getInstance().getInstanceId() + ")[" + instanceex.getInstance().getState().getName() + "]:" + Tools.getTagValue(instanceex.getInstance().getTags(), "NAME"), true, 5);
        if (instanceex.getInstance().getIamInstanceProfile() != null) {
          writeln("iam role:" + instanceex.getInstance().getIamInstanceProfile().getArn(), false, 6);
        }
        displaySecurityGroup(instanceex.getSecurityGroups(), 6);
        closeFolder(5);
      }
      closeFolder(4);
    }
    closeFolder(3);
  }
}
