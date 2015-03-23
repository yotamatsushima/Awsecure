package model;

import com.amazonaws.services.ec2.model.NetworkAcl;
import com.amazonaws.services.ec2.model.Subnet;
import java.util.ArrayList;
import java.util.List;

public class SubnetEx
{
  private Subnet subnet = new Subnet();
  private List<NetworkAcl> nacls = new ArrayList();
  private List<InstanceEx> instances = new ArrayList();
  
  public void setSubnet(Subnet s)
  {
    this.subnet = s;
  }
  
  public Subnet getSubnet()
  {
    return this.subnet;
  }
  
  public void addNacl(NetworkAcl nacl)
  {
    this.nacls.add(nacl);
  }
  
  public List<NetworkAcl> getNacls()
  {
    return this.nacls;
  }
  
  public void addInstance(InstanceEx iex)
  {
    this.instances.add(iex);
  }
  
  public List<InstanceEx> getInstances()
  {
    return this.instances;
  }
}
