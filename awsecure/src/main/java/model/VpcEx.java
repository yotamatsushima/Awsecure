package model;

import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import java.util.ArrayList;
import java.util.List;

public class VpcEx
{
  private Vpc vpc = new Vpc();
  private List<SubnetEx> subnets = new ArrayList();
  private List<LoadBalancerDescription> elbs = new ArrayList();
  
  public void setVpc(Vpc v)
  {
    this.vpc = v;
  }
  
  public Vpc getVpc()
  {
    return this.vpc;
  }
  
  public void addSubnet(SubnetEx subnet)
  {
    this.subnets.add(subnet);
  }
  
  public List<SubnetEx> getSubnets()
  {
    return this.subnets;
  }
  
  public void addELB(LoadBalancerDescription elb)
  {
    this.elbs.add(elb);
  }
  
  public List<LoadBalancerDescription> getELBs()
  {
    return this.elbs;
  }
}
